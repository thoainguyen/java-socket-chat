package chatapp.server;

public class User{
	private String userName;
	private String userPass;
	private boolean online;
	
	public User(){}
	
	public User(String name, String pass){
		this.userName = name;
		this.userPass = pass;
	}
	
	public String getUserName(){
		return this.userName;
	}
	
	public boolean compareTo(User other){
		return other.userName == this.userName;
	}
	
	public void setOnline(boolean bool){
		this.online = bool;
	}
	
	public boolean isOnline(){
		return this.online;
	}
	
	public boolean isAuthenticate(String name, String pass){
		return this.userName == name && this.pass = pass;
	}
}