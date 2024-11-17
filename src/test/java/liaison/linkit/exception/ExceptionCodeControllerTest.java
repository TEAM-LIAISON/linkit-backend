//package liaison.linkit.exception;
//
//import com.fasterxml.jackson.core.type.TypeReference;
//import com.fasterxml.jackson.databind.ObjectMapper;
//import liaison.linkit.global.ControllerTest;
//import liaison.linkit.global.exception.ExceptionCode;
//import org.junit.jupiter.api.DisplayName;
//import org.junit.jupiter.api.Test;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.test.autoconfigure.restdocs.AutoConfigureRestDocs;
//import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
//import org.springframework.boot.test.mock.mockito.MockBean;
//import org.springframework.data.jpa.mapping.JpaMetamodelMappingContext;
//import org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders;
//import org.springframework.restdocs.payload.FieldDescriptor;
//import org.springframework.test.web.servlet.MvcResult;
//
//import java.util.Arrays;
//import java.util.List;
//import java.util.Map;
//
//import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
//import static org.springframework.restdocs.snippet.Attributes.key;
//import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
//
//@WebMvcTest(ExceptionCodeController.class)
//@MockBean(JpaMetamodelMappingContext.class)
//@AutoConfigureRestDocs
//class ExceptionCodeControllerTest extends ControllerTest {
//
//    @Autowired
//    private ObjectMapper objectMapper;
//
//    @DisplayName("작성된 커스텀 예외 코드를 반환한다.")
//    @Test
//    void getExceptionCodes() throws Exception {
//        // when & then
//        final MvcResult mvcResult = mockMvc.perform(RestDocumentationRequestBuilders.get("/errors"))
//                .andExpect(status().isOk())
//                .andDo(restDocs.document(
//                        new ExceptionResponseFieldsSnippet(
//                                "exception-response",
//                                convertExceptionCodesToFieldDescriptors(ExceptionCode.values()),
//                                true
//                        )
//                )).andReturn();
//
//        final Map<String, ExceptionResponse> exceptionResponses = objectMapper.readValue(
//                mvcResult.getResponse().getContentAsString(),
//                new TypeReference<>() {
//                }
//        );
//    }
//
//    private List<FieldDescriptor> convertExceptionCodesToFieldDescriptors(final ExceptionCode[] exceptionCodes) {
//        return Arrays.stream(Arrays.stream(exceptionCodes)
//                .map(code -> fieldWithPath(code.name()).description(code.getMessage())
//                        .attributes(
//                                key("code").value(code.getCode()),
//                                key("message").value(code.getMessage()))
//                ).toArray(FieldDescriptor[]::new)).toList();
//    }
//}
