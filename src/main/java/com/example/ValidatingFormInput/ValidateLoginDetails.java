package com.example.ValidatingFormInput;

import jakarta.validation.constraints.NotNull;

public class ValidateLoginDetails {
    

    @NotNull
	private String password;

    @NotNull
    private String username;

    public String getEmail() {
		return this.username;
	}

	public void setEmail(String username) {
		this.username= username;
	}
    public String getPassword() {
		return this.password;
	}

	public void setPassword(String password) {
		this.password= password;
	}

}
