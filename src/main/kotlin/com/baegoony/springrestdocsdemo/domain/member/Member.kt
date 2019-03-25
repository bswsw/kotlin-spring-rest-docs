package com.baegoony.springrestdocsdemo.domain.member

import com.baegoony.springrestdocsdemo.base.BaseEntity
import com.baegoony.springrestdocsdemo.domain.team.Team
import javax.persistence.Column
import javax.persistence.Entity
import javax.persistence.ManyToOne

@Entity
class Member(
    @ManyToOne
    var team: Team,
    @Column(name = "member_name")
    var name: String
) : BaseEntity()
