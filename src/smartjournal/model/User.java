/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package smartjournal.model;

/**
 *
 * @author leecy
 */
public class User {
    
    private String email;
    private String displayName;
    private String password;
    
    public User(String email, String displayName, String password){
        this.email=email;
        this.displayName=displayName;
        this.password=password;
    }  
    
    public String getEmail(){
        return email;
    }
    
    public String getDisplayName(){
        return displayName;
    }
    
    public String getPassword(){
        return password;
    }
}
