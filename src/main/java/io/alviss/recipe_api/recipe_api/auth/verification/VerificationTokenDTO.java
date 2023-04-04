package io.alviss.recipe_api.recipe_api.auth.verification;


import io.alviss.recipe_api.recipe_api.user.User;
import lombok.*;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.sql.Timestamp;
import java.util.Calendar;
import java.util.Date;


@Getter
@Setter
@NoArgsConstructor
@EqualsAndHashCode
@ToString
public class VerificationTokenDTO {

    private static final int EXPIRATION = 60 * 24;

    private Long id;

    @NotBlank
    @NotNull
    private String token;

    @NotBlank
    @NotNull
    private User user;


    private Date expiryDate;

    public VerificationTokenDTO(String token, User user) {
        this.token = token;
        this.user = user;
        this.expiryDate = calculateExpiry(EXPIRATION);
    }

    private Date calculateExpiry (final int expiryTimeInMinutes) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(new Timestamp(cal.getTime().getTime()));
        cal.add(Calendar.MINUTE, expiryTimeInMinutes);
        return new Date(cal.getTime().getTime());
    }

    public VerificationToken mapDtoToEntity () {
        final VerificationToken token1 = new VerificationToken();
        token1.setToken(this.getToken());
        token1.setUser(this.getUser());
        token1.setExpiryDate(this.getExpiryDate());
        return token1;
    }

    public void updateToken (final String token) {
        this.setToken(token);
        this.expiryDate = calculateExpiry(EXPIRATION);
    }
}