package by.plisunov.scoreboard.model;

//@Entity
//@Table(name = "modx_user_group_roles")
public class Role {

	//@Id
	private int id;

	private String name;

	private String description;

	private int authority;

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public int getAuthority() {
		return authority;
	}

	public void setAuthority(int authority) {
		this.authority = authority;
	}

}