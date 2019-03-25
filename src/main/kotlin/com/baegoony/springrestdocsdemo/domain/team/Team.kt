package com.baegoony.springrestdocsdemo.domain.team

import com.baegoony.springrestdocsdemo.base.BaseEntity
import javax.persistence.Column
import javax.persistence.Entity

@Entity
class Team(
    @Column(name = "team_name")
    var name: String,
    var maker: String,
    var year: Int
) : BaseEntity()
