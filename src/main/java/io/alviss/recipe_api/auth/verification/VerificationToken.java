package io.alviss.recipe_api.auth.verification;

import io.alviss.recipe_api.user.User;
import lombok.*;

import javax.persistence.*;

import java.sql.Timestamp;
import java.util.Calendar;
import java.util.Date;
import java.util.UUID;

@Entity
@Data
@NoArgsConstructor
@EqualsAndHashCode(callSuper = false, exclude = "user")
public class VerificationToken {

    public VerificationToken (User user) {
        this.user = user;
        this.expiryDate = calculateExpiry(EXPIRATION);
    }

    public VerificationToken (UUID token, User user) {
        this.token = token;
        this.user = user;
        this.expiryDate = calculateExpiry(EXPIRATION);
    }
    
    private static final int EXPIRATION = 60 * 24;

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Column
    private UUID token = UUID.randomUUID();

    @OneToOne(targetEntity = User.class, optional = false)
    @JoinColumn(nullable = false, name = "user_id", unique = true)
    @ToString.Exclude
    private User user;

    @Column
    private Date expiryDate;

    private Date calculateExpiry (final int expiryTimeInMinutes) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(new Timestamp(cal.getTime().getTime()));
        cal.add(Calendar.MINUTE, expiryTimeInMinutes);
        return new Date(cal.getTime().getTime());
    }
}
