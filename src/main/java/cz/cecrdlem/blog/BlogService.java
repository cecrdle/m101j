/**
 * 
 */
package cz.cecrdlem.blog;

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Service;

import sun.misc.BASE64Encoder;
import cz.cecrdlem.blog.dao.SessionsRepository;
import cz.cecrdlem.blog.dao.UsersRepository;
import cz.cecrdlem.blog.to.Session;
import cz.cecrdlem.blog.to.User;
import cz.cecrdlem.blog.web.form.LoginForm;

/**
 * @author milan.cecrdle
 * 
 */
@Service
public class BlogService {
	@Autowired
	UsersRepository userRepository;
	@Autowired
	SessionsRepository sessionRepository;

	public String findUserNameBySessionId(String sessionId) {
		Session session = sessionRepository.findOne(sessionId);
		return session != null ? session.getUsername() : null;
	}

	public boolean addUser(User user) {
		String passwordHash = makePasswordHash(user.getPassword(), Integer.toString(new SecureRandom().nextInt()));
		user.setPassword(passwordHash);
		try {
			userRepository.save(user);
			return true;
		} catch (DuplicateKeyException e) {
			System.out.println("Username already in use: " + user.getUsername());
			return false;
		}
	}

	public String startSession(String username) {
		// get 32 byte random number. that's a lot of bits.
		SecureRandom generator = new SecureRandom();
		byte randomBytes[] = new byte[32];
		generator.nextBytes(randomBytes);
		BASE64Encoder encoder = new BASE64Encoder();
		String sessionID = encoder.encode(randomBytes);
		Session session = new Session();
		session.setUsername(username);
		session.setId(sessionID);
		sessionRepository.save(session);
		return sessionID;
	}

	public User validateLogin(LoginForm loginForm) {
		User user = userRepository.findOne(loginForm.getUsername());
		if (user == null) {
			System.out.println("User not in database");
			return null;
		}
		String salt = user.getPassword().split(",")[1];
		if (!user.getPassword().equals(makePasswordHash(loginForm.getPassword(), salt))) {
			System.out.println("Submitted password is not a match");
			return null;
		}

		return user;
	}

	public void endSession(String sessionID) {
		sessionRepository.delete(sessionID);
	}

	private String makePasswordHash(String password, String salt) {
		try {
			String saltedAndHashed = password + "," + salt;
			MessageDigest digest = MessageDigest.getInstance("MD5");
			digest.update(saltedAndHashed.getBytes());
			BASE64Encoder encoder = new BASE64Encoder();
			byte hashedBytes[] = (new String(digest.digest(), "UTF-8")).getBytes();
			return encoder.encode(hashedBytes) + "," + salt;
		} catch (NoSuchAlgorithmException e) {
			throw new RuntimeException("MD5 is not available", e);
		} catch (UnsupportedEncodingException e) {
			throw new RuntimeException("UTF-8 unavailable?  Not a chance", e);
		}
	}

}
