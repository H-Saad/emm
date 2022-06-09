package emm;

public class Attachement {
	private String path;
	private String name;
	
	public Attachement() {
		path="";name="";
	}
	
	public Attachement(String path, String name) {
		super();
		this.path = path;
		this.name = name;
	}
	public String getPath() {
		return path;
	}
	public void setPath(String path) {
		this.path = path;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}

	@Override
	public String toString() {
		return "Attachement [path=" + path + ", name=" + name + "]";
	}
	
}
