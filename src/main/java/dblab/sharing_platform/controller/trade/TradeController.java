package dblab.sharing_platform.controller.trade;

import dblab.sharing_platform.config.security.util.SecurityUtil;
import dblab.sharing_platform.dto.response.Response;
import dblab.sharing_platform.dto.trade.TradePagingCondition;
import dblab.sharing_platform.dto.trade.TradeRequestDto;
import dblab.sharing_platform.service.trade.TradeService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

import static dblab.sharing_platform.config.security.util.SecurityUtil.getCurrentUsernameCheck;
import static org.springframework.http.HttpStatus.CREATED;
import static org.springframework.http.HttpStatus.OK;

@Api(value = "Trade Controller", tags = "Trade")
@RestController
@RequiredArgsConstructor
@RequestMapping("/trades")
public class TradeController {
    private final TradeService tradeService;

    @ApiOperation(value = "Post ID로 Trade 생성", notes = "게시글 ID로 Trade를 생성합니다.")
    @PostMapping("/{postId}")
    @ResponseStatus(CREATED)
    public Response createTradeByPostId(@Valid @RequestBody TradeRequestDto tradeRequestDto,
                                @ApiParam(value = "Post ID", required = true) @PathVariable Long postId) {
        return Response.success(CREATED.value(), tradeService.createTradeByPostId(tradeRequestDto, postId, getCurrentUsernameCheck()));
    }

    @ApiOperation(value = "Trade ID로 Trade의 거래완료", notes = "Trade ID로 해당 Trade의 거래를 완료합니다.")
    @PatchMapping("/{tradeId}")
    @ResponseStatus(OK)
    public Response completeTradeByTradeId(@ApiParam(value = "완료할 Trade ID", required = true) @PathVariable Long tradeId){
        return Response.success(OK.value(), tradeService.completeTradeByTradeId(tradeId));
    }

    @ApiOperation(value = "Trade ID로 거래를 단건 조회", notes = "Trade ID로 거래를 조회합니다.")
    @GetMapping("/{tradeId}")
    @ResponseStatus(OK)
    public Response findSingleTradeById(@ApiParam(value = "조회할 Trade ID", required = true) @PathVariable Long tradeId){
        return Response.success(OK.value(), tradeService.findSingleTradeById(tradeId));
    }

    @ApiOperation(value = "Trade 전체 조회", notes = "모든 Trade를 조회합니다 / ADMIN 전용")
    @GetMapping
    @ResponseStatus(OK)
    public Response findAllTradeByAdmin(@Valid TradePagingCondition cond){
        return Response.success(OK.value(), tradeService.findAllTradeByAdmin(cond));
    }

    @ApiOperation(value = "나의 완료된/완료되지 않은 수여 거래 내역 조회", notes = "내가 대여 해줄 거래 중 완료된/아직 완료되지 않은 거래 내역 조회")
    @GetMapping("/rend-item")
    @ResponseStatus(OK)
    public Response findAllRendTradeByCurrentUser(@Valid TradePagingCondition cond){
        cond.setRenderMember(SecurityUtil.getCurrentUsernameCheck());
        return Response.success(OK.value(), tradeService.findAllRendTradeByCurrentUser(cond));
    }

    @ApiOperation(value = "나의 완료된/완료되지 않은 대여 거래 내역 조회", notes = "내가 대여 받을 거래 중 완료된/아직 완료되지 않은 거래 내역 조회")
    @GetMapping("/borrow-item")
    @ResponseStatus(OK)
    public Response findAllBorrowTradeByCurrentUser(@Valid TradePagingCondition cond){
        cond.setBorrowerMember(SecurityUtil.getCurrentUsernameCheck());
        return Response.success(OK.value(), tradeService.findAllBorrowTradeByCurrentUser(cond));
    }

    @ApiOperation(value = "Trade ID로 거래를 삭제", notes = "Trade ID로 거래를 삭제합니다.")
    @DeleteMapping("/{tradeId}")
    @ResponseStatus(OK)
    public Response cancelTradeByRenderWithTradeId(@ApiParam(value = "삭제 Trade ID", required = true) @PathVariable Long tradeId){
        tradeService.deleteTradeByRenderWithTradeId(tradeId);
        return Response.success(OK.value());
    }
}