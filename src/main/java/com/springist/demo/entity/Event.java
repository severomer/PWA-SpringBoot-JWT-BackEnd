package com.springist.demo.entity;

import java.util.Collection;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import org.springframework.format.annotation.DateTimeFormat;

import com.fasterxml.jackson.annotation.JsonBackReference;

@Entity
@Table(name = "event")
public class Event {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name="id")
	private Long id;
	
	@Column(name="eventname")
	private String event_name;
	
	@DateTimeFormat(pattern = "dd-MM-yyyy'T'HH:mm:ss")
	@Column(name="eventdate")
	private Date event_date;

	@Column(name="ownerid")
	private Long userId;
	
	@Column(name="lat")
	private String elat;

	@Column(name="lng")
	private String elng;
	
	@Column(name="isprivate")
	private Boolean ozel;
	
	
    @OneToMany(mappedBy = "primaryKey.event",
            cascade = CascadeType.ALL)	
	@JsonBackReference
	private Set<EventUser> eventUsers = new HashSet<EventUser>();

	@ManyToMany(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
	@JoinTable(name = "event_members", 
	joinColumns = @JoinColumn(name = "event_id"), 
	inverseJoinColumns = @JoinColumn(name = "user_id"))

	private Collection<User> members;
	
	
	public Collection<User> getMembers() {
		return members;
	}

	public void setMembers(Collection<User> members) {
		this.members = members;
	}

	public Boolean getOzel() {
		return ozel;
	}

	public void setOzel(Boolean ozel) {
		this.ozel = ozel;
	}

	public Event(Long id, String event_name, Date event_date, Long userId, String elat, String elng) {
		this.id = id;
		this.event_name = event_name;
		this.event_date = event_date;
		this.userId = userId;
		this.elat = elat;
		this.elng = elng;
	}

	public String getElat() {
		return elat;
	}

	public void setElat(String elat) {
		this.elat = elat;
	}

	public String getElng() {
		return elng;
	}

	public void setElng(String elng) {
		this.elng = elng;
	}

	public Long getUserId() {
		return userId;
	}

	public void setUserId(Long userId) {
		this.userId = userId;
	}

	public String getEvent_name() {
		return event_name;
	}

	public void setEvent_name(String event_name) {
		this.event_name = event_name;
	}

	public Date getEvent_date() {
		return event_date;
	}

	public void setEvent_date(Date event_date) {
		this.event_date = event_date;
	}

	public Long getId() {
		return id;
	}

	public Event() {
		
	}
	public Event(String event_name, Date event_date) {

		this.event_name = event_name;
		this.event_date = event_date;
	}

	@Override
	public String toString() {
		return "Event [id=" + id + ", event_name=" + event_name + ", event_date=" + event_date + "]";
	}


    public Set<EventUser> getEventUsers() {
        return eventUsers;
    }
 
    public void setEventUsers(Set<EventUser> events) {
        this.eventUsers = events;
    }
     
    public void addEventUser(EventUser eventUser) {
        this.eventUsers.add(eventUser);
    }  
}
