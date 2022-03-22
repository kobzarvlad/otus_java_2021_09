package ru.otus.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

@Entity
@Table(name = "phone")
public class Phone implements Cloneable {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(name = "number")
  private String number;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "client_id")
  private Client client;

  public Phone() {
  }

  public Phone(String number) {
    this.number = number;
  }

  public Phone(Long id, String number) {
    this.id = id;
    this.number = number;
  }

  public Phone(Long id, String number, Client client) {
    this.id = id;
    this.number = number;
    this.client = client;
  }

  public Long getId() {
    return id;
  }

  public void setId(Long id) {
    this.id = id;
  }

  public String getNumber() {
    return number;
  }

  public void setNumber(String number) {
    this.number = number;
  }

  public Client getClient() {
    return client;
  }

  public void setClient(Client client) {
    this.client = client;
  }

  @Override
  protected Phone clone() {
    return new Phone(this.id, this.number, this.client);
  }

  @Override
  public String toString() {
    return "Phone{" +
        "id=" + id +
        ", number='" + number + '\'' +
        '}';
  }
}
