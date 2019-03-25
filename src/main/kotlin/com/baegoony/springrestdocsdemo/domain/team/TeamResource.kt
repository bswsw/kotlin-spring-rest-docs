package com.baegoony.springrestdocsdemo.domain.team

import com.baegoony.springrestdocsdemo.controller.TeamController
import org.springframework.hateoas.Resource
import org.springframework.hateoas.core.Relation
import org.springframework.hateoas.mvc.ControllerLinkBuilder.linkTo

class TeamResource(team: Team) : Resource<Team>(team) {

    init {
        this.add(linkTo(TeamController::class.java).slash(team.id).withSelfRel())
        this.add(linkTo(TeamController::class.java).withRel("list"))
    }
}
