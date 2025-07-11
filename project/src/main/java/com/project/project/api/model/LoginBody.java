package com.project.project.api.model;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public class LoginBody {

	@NotNull
	@NotBlank
    private String username;
	@NotNull
	@NotBlank
    private String password;
    //private String email;

	public String getUsername() {
		return this.username;
	}

	//public void setUsername(String username) {
	//	this.username = username;
	//}

	public String getPassword() {
		return this.password;
	}

	//public void setPassword(String password) {
	//	this.password = password;
	//}

	//public String getEmail() {
	//	return this.email;
	//}

	//public void setEmail(String email) {
	//	this.email = email;
	//}

}
