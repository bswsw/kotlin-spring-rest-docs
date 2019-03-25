package com.baegoony.springrestdocsdemo.controller

import com.baegoony.springrestdocsdemo.BaseControllerTest
import com.baegoony.springrestdocsdemo.domain.team.Team
import com.baegoony.springrestdocsdemo.domain.team.TeamRepository
import org.hamcrest.CoreMatchers.containsString
import org.junit.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.hateoas.MediaTypes
import org.springframework.http.MediaType
import org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document
import org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.get
import org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.post
import org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath
import org.springframework.restdocs.payload.PayloadDocumentation.requestFields
import org.springframework.restdocs.payload.PayloadDocumentation.responseFields
import org.springframework.restdocs.request.RequestDocumentation
import org.springframework.restdocs.request.RequestDocumentation.*
import org.springframework.test.web.servlet.result.MockMvcResultHandlers.print
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status
import java.util.Date


class TeamControllerTest : BaseControllerTest() {

    @Autowired
    private lateinit var teamRepository: TeamRepository

    companion object {
        private const val END_POINT = "teams"
    }

    @Test
    fun create_OK() {
        // given
        val body = mapOf(
            "name" to "그래들",
            "maker" to "나이키",
            "year" to 2000
        )

        // when
        val result = this.mockMvc.perform(
            post("/$END_POINT")
                .contentType(MediaType.APPLICATION_JSON_UTF8)
                .accept(MediaTypes.HAL_JSON_UTF8)
                .content(this.objectMapper.writeValueAsBytes(body))
        )

        // then
        result
            .andExpect(status().isCreated)
            .andExpect(jsonPath("name").value(body.getValue("name")))
            .andExpect(jsonPath("maker").value(body.getValue("maker")))
            .andExpect(jsonPath("year").value(body.getValue("year")))
            .andDo(print())
            .andDo(
                document(
                    "$END_POINT/create",
                    requestFields(
                        fieldWithPath("name").description("팀명"),
                        fieldWithPath("maker").description("브랜드"),
                        fieldWithPath("year").description("연식")
                    ),
                    responseFields(
                        fieldWithPath("id").description("키"),
                        fieldWithPath("name").description("팀명"),
                        fieldWithPath("maker").description("브랜드"),
                        fieldWithPath("year").description("연식"),
                        fieldWithPath("state").description("상태"),
                        fieldWithPath("createdAt").description("생성일시"),
                        fieldWithPath("updatedAt").description("수정일시"),
                        fieldWithPath("_links.self.href").description("조회링크"),
                        fieldWithPath("_links.list.href").description("목록링크")
                    )
                )
            )


    }

    @Test
    fun list_OK() {
        // given
        repeat(10) {
            this.generate()
        }

        // when
        val result = this.mockMvc.perform(
            get("/$END_POINT")
                .param("page", "2")
                .param("size", "2")
        )

        // then
        result
            .andExpect(status().isOk)
            .andExpect(jsonPath("_embedded.teamList[0].name", containsString("name")))
            .andExpect(jsonPath("_embedded.teamList[0].maker", containsString("bugs")))
            .andDo(print())
            .andDo(
                document(
                    "$END_POINT/list",
                    requestParameters(
                        parameterWithName("page").description("페이지 번호"),
                        parameterWithName("size").description("페이지 사이즈")
                    ),
                    responseFields(
                        fieldWithPath("_embedded.teamList[].id").description("팀키"),
                        fieldWithPath("_embedded.teamList[].name").description("팀명"),
                        fieldWithPath("_embedded.teamList[].maker").description("브랜드명"),
                        fieldWithPath("_embedded.teamList[].year").description("연식"),
                        fieldWithPath("_embedded.teamList[].state").description("상태"),
                        fieldWithPath("_embedded.teamList[].createdAt").description("생성일시"),
                        fieldWithPath("_embedded.teamList[].updatedAt").description("수정일시"),
                        fieldWithPath("_embedded.teamList[]._links.self.href").description("조회링크"),
                        fieldWithPath("_embedded.teamList[]._links.list.href").description("수정"),
                        fieldWithPath("_links.first.href").description("첫 페이지 링크"),
                        fieldWithPath("_links.prev.href").description("이전 페이지 링크"),
                        fieldWithPath("_links.self.href").description("현재 페이지 링크"),
                        fieldWithPath("_links.next.href").description("다음 페이지 링크"),
                        fieldWithPath("_links.last.href").description("마지막 페이지 링크"),
                        fieldWithPath("page.size").description("페이지 사이즈"),
                        fieldWithPath("page.totalElements").description("총 개수"),
                        fieldWithPath("page.totalPages").description("총 페이지"),
                        fieldWithPath("page.number").description("현재 페이지")
                    )
                )
            )
    }

    private fun generate(): Team {
        val time = Date().time
        return this.teamRepository.save(
            Team(
                name = "name $time",
                maker = "bugs $time",
                year = 2019
            )
        )
    }
}
