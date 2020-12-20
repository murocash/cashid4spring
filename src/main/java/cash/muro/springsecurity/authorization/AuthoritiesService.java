package cash.muro.springsecurity.authorization;

import java.util.Collection;

import org.springframework.security.core.GrantedAuthority;

public interface AuthoritiesService {
	
	Collection<GrantedAuthority> authorities(String userId);

}
