package com.baegoony.springrestdocsdemo.controller

import com.baegoony.springrestdocsdemo.domain.team.Team
import com.baegoony.springrestdocsdemo.domain.team.TeamBody
import com.baegoony.springrestdocsdemo.domain.team.TeamRepository
import com.baegoony.springrestdocsdemo.domain.team.TeamResource
import org.modelmapper.ModelMapper
import org.springframework.data.domain.Pageable
import org.springframework.data.web.PagedResourcesAssembler
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import java.net.URI

@RestController
@RequestMapping("/teams")
class TeamController(
    private val teamRepository: TeamRepository,
    private val modelMapper: ModelMapper
) {

    @PostMapping
    fun create(@RequestBody body: TeamBody): ResponseEntity<*> {
        val team = this.modelMapper.map(body, Team::class.java)
        val savedTeam = this.teamRepository.save(team)
        val resource = TeamResource(savedTeam)

        return ResponseEntity.created(URI(resource.id.href)).body(resource)
    }

    @GetMapping
    fun list(
        pageable: Pageable,
        pagedResourcesAssembler: PagedResourcesAssembler<Team>
    ): ResponseEntity<*> {
        val team = this.teamRepository.findAll(pageable)

        return ResponseEntity.ok(
            pagedResourcesAssembler.toResource(
                team,
                ::TeamResource
            )
        )
    }
}
