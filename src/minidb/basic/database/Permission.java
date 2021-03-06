package minidb.basic.database;

import java.io.Serializable;

public class Permission implements Serializable{

	private static final long serialVersionUID = 1L;

	public boolean canSelect;
	public boolean canUpdate;
	public boolean canGrantSelect;
	public boolean canGrantUpdate;

	public Permission() {
		this.canGrantSelect=false;
		this.canGrantUpdate=false;
		this.canSelect=false;
		this.canUpdate=false;
	}
		
	public void revoke(Permission p) {
		this.canSelect=this.canSelect&&p.canSelect;
		this.canUpdate=this.canUpdate&&p.canUpdate;
		this.canGrantSelect=this.canGrantSelect&&p.canGrantSelect;
		this.canGrantUpdate=this.canGrantUpdate&&p.canGrantUpdate;
	}
	public void merge(Permission p) {
		this.canSelect=this.canSelect||p.canSelect;
		this.canUpdate=this.canUpdate||p.canUpdate;
		this.canGrantSelect=this.canGrantSelect||p.canGrantSelect;
		this.canGrantUpdate=this.canGrantUpdate||p.canGrantUpdate;
	}

}
