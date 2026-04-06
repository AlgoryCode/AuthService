package com.ael.authservice.service;

import com.ael.authservice.dto.response.TwoFactorSetupResponse;
import com.ael.authservice.model.User;
import com.ael.authservice.repository.UserRepository;
import dev.samstevens.totp.code.CodeGenerator;
import dev.samstevens.totp.code.CodeVerifier;
import dev.samstevens.totp.code.DefaultCodeGenerator;
import dev.samstevens.totp.code.DefaultCodeVerifier;
import dev.samstevens.totp.code.HashingAlgorithm;
import dev.samstevens.totp.qr.QrData;
import dev.samstevens.totp.qr.QrGenerator;
import dev.samstevens.totp.qr.ZxingPngQrGenerator;
import dev.samstevens.totp.secret.DefaultSecretGenerator;
import dev.samstevens.totp.secret.SecretGenerator;
import dev.samstevens.totp.time.SystemTimeProvider;
import dev.samstevens.totp.time.TimeProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Base64;

@Service
@RequiredArgsConstructor
public class TwoFactorService {

    private static final String TOTP_ISSUER = "AuthService";

    private final UserRepository userRepository;

    private final SecretGenerator secretGenerator = new DefaultSecretGenerator();
    private final QrGenerator qrGenerator = new ZxingPngQrGenerator();
    private final TimeProvider timeProvider = new SystemTimeProvider();
    private final CodeGenerator codeGenerator = new DefaultCodeGenerator(HashingAlgorithm.SHA1, 6);
    private final CodeVerifier codeVerifier = new DefaultCodeVerifier(codeGenerator, timeProvider);

    private record PreparedTotpSetup(byte[] qrPng, String secret, String accountLabel) {}

    private PreparedTotpSetup prepareTotpSetupForUser(Integer userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));

        if (user.isTwoFactorEnabled()) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "2FA is already enabled; disable it first to regenerate");
        }

        String secret = secretGenerator.generate();
        user.setTotpSecret(secret);
        user.setTwoFactorEnabled(false);
        userRepository.save(user);

        QrData qrData = new QrData.Builder()
                .label(user.getEmail())
                .secret(secret)
                .issuer(TOTP_ISSUER)
                .algorithm(HashingAlgorithm.SHA1)
                .digits(6)
                .period(30)
                .build();

        try {
            byte[] png = qrGenerator.generate(qrData);
            return new PreparedTotpSetup(png, secret, user.getEmail());
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Failed to generate QR code", e);
        }
    }

    private static String pctEncode(String s) {
        return URLEncoder.encode(s, StandardCharsets.UTF_8).replace("+", "%20");
    }

    /** Google Authenticator key-uri formatı (QR ile aynı yük). */
    private static String buildOtpAuthUri(String secret, String accountLabel, String issuer) {
        String label = issuer + ":" + accountLabel;
        return "otpauth://totp/" + pctEncode(label)
                + "?secret=" + secret
                + "&issuer=" + pctEncode(issuer)
                + "&algorithm=SHA1&digits=6&period=30";
    }

    @Transactional
    public byte[] prepareSecretAndQrPng(Integer userId) {
        return prepareTotpSetupForUser(userId).qrPng();
    }

    /**
     * PNG (Base64), gizli anahtar ve otpauth URI — tek telefonda manuel kurulum / uygulama bağlantısı için.
     */
    @Transactional
    public TwoFactorSetupResponse prepareSecretAndSetupPayload(Integer userId) {
        PreparedTotpSetup p = prepareTotpSetupForUser(userId);
        String b64 = Base64.getEncoder().encodeToString(p.qrPng());
        String uri = buildOtpAuthUri(p.secret(), p.accountLabel(), TOTP_ISSUER);
        return new TwoFactorSetupResponse(p.secret(), TOTP_ISSUER, p.accountLabel(), b64, uri);
    }

    @Transactional
    public void activateWithTotp(Integer userId, String rawCode) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));

        if (user.isTwoFactorEnabled()) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "2FA is already active");
        }

        String secret = user.getTotpSecret();
        if (secret == null || secret.isBlank()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Run /2fa/setup or /2fa/enabled first");
        }

        String code = rawCode == null ? "" : rawCode.replaceAll("\\s+", "");
        if (!code.matches("\\d{6}")) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid code format");
        }

        boolean valid;

        valid = codeVerifier.isValidCode(secret, code);

        if (!valid) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid verification code");
        }

        user.setTwoFactorEnabled(true);
        userRepository.save(user);
    }

    @Transactional
    public void disableWithTotp(Integer userId, String rawCode) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));

        if (!user.isTwoFactorEnabled()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "2FA is not enabled");
        }

        String secret = user.getTotpSecret();
        if (secret == null || secret.isBlank()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "No TOTP secret; reset account 2FA via support");
        }

        String code = rawCode == null ? "" : rawCode.replaceAll("\\s+", "");
        if (!code.matches("\\d{6}")) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid code format");
        }

        if (!codeVerifier.isValidCode(secret, code)) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid verification code");
        }

        user.setTwoFactorEnabled(false);
        user.setTotpSecret(null);
        userRepository.save(user);
    }
}
