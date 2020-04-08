package com.ulstu.pharmacy.pmmsl.common.entity;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import javax.persistence.Access;
import javax.persistence.AccessType;
import javax.persistence.MappedSuperclass;
import java.io.Serializable;

@Getter
@Setter
@ToString
@MappedSuperclass
public abstract class AbstractEntity<Id extends Serializable> implements Serializable {

    @javax.persistence.Id
    @Access(AccessType.PROPERTY)
    protected Id id;
}