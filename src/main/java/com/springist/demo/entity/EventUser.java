package com.springist.demo.entity;

import java.util.Date;

import javax.persistence.AssociationOverride;
import javax.persistence.AssociationOverrides;
import javax.persistence.Column;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Transient;

@Entity
@Table(name = "EVENT_USER")
@AssociationOverrides({
    @AssociationOverride(name = "primaryKey.user",
        joinColumns = @JoinColumn(name = "USER_ID")),
    @AssociationOverride(name = "primaryKey.event",
        joinColumns = @JoinColumn(name = "EVENT_ID")) })
public class EventUser {

//	@JsonManagedReference
	private EventUserId primaryKey = new EventUserId();
	
	private boolean  attended;
	
	private Date memberDate;

	@EmbeddedId
	public EventUserId getPrimaryKey() {
		return primaryKey;
	}

	public void setPrimaryKey(EventUserId primaryKey) {
		this.primaryKey = primaryKey;
	}
	
	 @Transient
	    public User getUser() {
	        return getPrimaryKey().getUser();
	    }
	 
	    public void setUser(User user) {
	        getPrimaryKey().setUser(user);
	    }
	    
		
		 @Transient
		    public Event getEvent() {
		        return getPrimaryKey().getEvent();
		    }
		 
		    public void setEvent(Event event) {
		        getPrimaryKey().setEvent(event);
		    }
		    
		    
			 @Transient
	public boolean isAttended() {
		return attended;
	}
	
	

	public boolean getAttended() {
		return attended;
	}

	public void setAttended(boolean attended) {
		this.attended = attended;
	}

    @Column(name = "MEMBER_DATE")
    @Temporal(TemporalType.DATE)
	public Date getMemberDate() {
		return memberDate;
	}

	public void setMemberDate(Date memberDate) {
		this.memberDate = memberDate;
	}

	@Override
	public String toString() {
		return "EventUser [primaryKey=" + primaryKey + ", attended=" + attended + ", memberDate=" + memberDate + "]";
	}
	
	
}
