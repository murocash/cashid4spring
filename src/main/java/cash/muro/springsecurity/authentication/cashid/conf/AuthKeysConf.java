package cash.muro.springsecurity.authentication.cashid.conf;

import java.time.Duration;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class AuthKeysConf {
	private Duration timeToExpire = Duration.ofMillis(30000);
	private int bits = 48;
}
