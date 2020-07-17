package uk.gov.gsi.justice.spg.stub.json;

import com.fasterxml.jackson.annotation.JsonProperty;

public class UploadnewResponse {
	
	private String id;
	private String crn;
	private String name;
	private String creationDate;
	private String lastModifiedDate;
	private String modifier;
	private String author;
	private String locked;
	private String lockOwner;
	private String reserved;
	private String reservationOwner;
	private String entityType;
	private String entityId;
	private String docType;
	private String url;
	public String getId() {
		return id;
	}
	
	@JsonProperty("ID")
	public void setId(String id) {
		this.id = id;
	}
	
	@JsonProperty("CRN")
	public String getCrn() {
		return crn;
	}
	public void setCrn(String crn) {
		this.crn = crn;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getCreationDate() {
		return creationDate;
	}
	public void setCreationDate(String creationDate) {
		this.creationDate = creationDate;
	}
	public String getLastModifiedDate() {
		return lastModifiedDate;
	}
	public void setLastModifiedDate(String lastModifiedDate) {
		this.lastModifiedDate = lastModifiedDate;
	}
	public String getModifier() {
		return modifier;
	}
	public void setModifier(String modifier) {
		this.modifier = modifier;
	}
	public String getAuthor() {
		return author;
	}
	public void setAuthor(String author) {
		this.author = author;
	}
	public String getLocked() {
		return locked;
	}
	public void setLocked(String locked) {
		this.locked = locked;
	}
	public String getLockOwner() {
		return lockOwner;
	}
	public void setLockOwner(String lockOwner) {
		this.lockOwner = lockOwner;
	}
	public String getReserved() {
		return reserved;
	}
	public void setReserved(String reserved) {
		this.reserved = reserved;
	}
	public String getReservationOwner() {
		return reservationOwner;
	}
	public void setReservationOwner(String reservationOwner) {
		this.reservationOwner = reservationOwner;
	}
	public String getEntityType() {
		return entityType;
	}
	public void setEntityType(String entityType) {
		this.entityType = entityType;
	}
	public String getEntityId() {
		return entityId;
	}
	public void setEntityId(String entityId) {
		this.entityId = entityId;
	}
	public String getDocType() {
		return docType;
	}
	public void setDocType(String docType) {
		this.docType = docType;
	}
	public String getUrl() {
		return url;
	}
	public void setUrl(String url) {
		this.url = url;
	}
	
}
