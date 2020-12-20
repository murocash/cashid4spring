package cash.muro.springsecurity.authentication.cashid.conf;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;

import cash.muro.springsecurity.authentication.cashid.service.CashIdCredentialsService;
import lombok.extern.slf4j.Slf4j;

@Configuration
@EnableScheduling
@Slf4j
public class ScheduledTasks {

	@Autowired
	private CashIdCredentialsService credentialsService;

	@Scheduled(fixedRate = 10000)
	public void removeExpiredNonces() {
		log.info("removing expired nonces.");
		credentialsService.removeExpiredCredentials();
	}

}
