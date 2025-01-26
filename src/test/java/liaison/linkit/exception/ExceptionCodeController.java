//package liaison.linkit.exception;
//
//import org.springframework.http.ResponseEntity;
//import org.springframework.web.bind.annotation.GetMapping;
//import org.springframework.web.bind.annotation.RequestMapping;
//import org.springframework.web.bind.annotation.RestController;
//
//import java.util.Arrays;
//import java.util.stream.Collectors;
//
//@RestController
//@RequestMapping("/errors")
//public class ExceptionCodeController {
//
//    @GetMapping
//    public ResponseEntity<Map<String, ExceptionResponse>> getExceptionCodes() {
//        final Map<String, ExceptionResponse> exceptionResponses = Arrays.stream(ExceptionCode.values())
//                .collect(Collectors.toMap(
//                        Enum::name,
//                        code -> new ExceptionResponse(code.getCode(), code.getMessage())
//                ));
//        return ResponseEntity.ok().body(exceptionResponses);
//    }
//}
