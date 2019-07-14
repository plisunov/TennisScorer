package by.plisunov.scoreboard.model;

import lombok.Getter;
import lombok.Setter;

//@Entity
@Getter
@Setter
// @Table(name = "modx_users")
public class User {

    //@Id
    private int id;

    private String username;

    private String password;

    private String cachepwd;

    private String class_key;

    private byte active;

    private String remote_key;

    private String remote_data;

    private String hash_class;

    private String salt;

    private int primary_group;

    private String session_stale;

    private byte sudo;

}
