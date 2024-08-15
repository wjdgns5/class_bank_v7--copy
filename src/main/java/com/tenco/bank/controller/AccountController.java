package com.tenco.bank.controller;

import java.util.Arrays;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.tenco.bank.dto.DepositDTO;
import com.tenco.bank.dto.SaveDTO;
import com.tenco.bank.dto.TransferDTO;
import com.tenco.bank.dto.WithdrawalDTO;
import com.tenco.bank.handler.exception.DataDeliveryException;
import com.tenco.bank.handler.exception.UnAuthorizedException;
import com.tenco.bank.repository.model.Account;
import com.tenco.bank.repository.model.HistoryAccount;
import com.tenco.bank.repository.model.User;
import com.tenco.bank.service.AccountService;
import com.tenco.bank.utils.Define;

import jakarta.servlet.http.HttpSession;

@Controller
@RequestMapping("/account")
public class AccountController {

	// 계좌 생성 화면 요청 -DI 처리

	@Autowired
	private HttpSession session;

	// 여기에 AccountService 있어야 하는데 없음 ... 추후 만들예정
	@Autowired
	private AccountService accountService;

	/**
	 * 계좌 생성 페이지 요청 주소 설계 : http://localhost:8080/account/save
	 * 
	 * @return
	 */
	@GetMapping("/save")
	public String savePage() {

		// 1. 인증 검사가 필요 ( account 전체가 필요하다.)
		User principal = (User) session.getAttribute(Define.PRINCIPAL);

		if (principal == null) { // User 세션에 있는 값이 null 이라는 조건
			throw new UnAuthorizedException(Define.NOT_AN_AUTHENTICATED_USER, HttpStatus.UNAUTHORIZED);
		}

		return "account/save";
	} // end of savePage()

	/**
	 * 계좌 생성 기능 요청 주소 설계 : http://localhost:8080/account/save
	 * 
	 * @param dto
	 * @return 추후 계좌 목록 페이지 이동 처리
	 */
	@PostMapping("/save")
	public String savePage(SaveDTO dto) {
		// 1. form 데이터 추출 (계좌번호, 비밀번호, 계좌잔액?) 파싱전략
		// 2. 인증검사 --> User 세션 principal 에 있는 데이터 null 인지 비교
		// 3. 유효성 검사 --> form 데이터를 받았지만 이상하게 받았는지 아닌지 컨트롤러에서 처리
		// 4. 서비스 호출 --> 제대로된 데이터 양식을 받게되면 서비스에서 처리 후 컨트롤러로 다시 온다.

		User principal = (User) session.getAttribute(Define.PRINCIPAL); // 유저 세션 가져옴

		if (principal == null) { // 유저 세션을 통한 인증검사
			throw new UnAuthorizedException(Define.NOT_AN_AUTHENTICATED_USER, HttpStatus.UNAUTHORIZED);
		}

		if (dto.getNumber() == null || dto.getNumber().trim().isEmpty()) {
			throw new DataDeliveryException(Define.ENTER_YOUR_ACCOUNT_NUMBER, HttpStatus.BAD_REQUEST);
		}

		if (dto.getPassword() == null || dto.getPassword().trim().isEmpty()) {
			throw new DataDeliveryException(Define.ENTER_YOUR_PASSWORD, HttpStatus.BAD_REQUEST);
		}

		if (dto.getBalance() == null || dto.getBalance() <= 0) {
			throw new DataDeliveryException(Define.ENTER_YOUR_BALANCE, HttpStatus.BAD_REQUEST);
		}

		accountService.createAccount(dto, principal.getId());

		return "redirect:/index";
	} // end of savePage()

	/**
	 * 계좌 목록 화면 요청 주소 설계 : http://localhost:8080/account/list 화면 : list.jsp
	 * 
	 * @return
	 */
	@GetMapping({ "/list", "/" })
	public String listPage(Model model) {

		// 유저 세션 불러오기
		User principal = (User) session.getAttribute(Define.PRINCIPAL);
		// 1. 인증검사
		if (principal == null) {
			throw new DataDeliveryException(Define.NOT_AN_AUTHENTICATED_USER, HttpStatus.UNAUTHORIZED);
		}

		// 2. 유효성 검사
		// 3. 서비스 호출
		List<Account> accountList = accountService.readAccountListByUserId(principal.getId());
		if (accountList.isEmpty()) {
			model.addAttribute("accountList", null);
		} else {
			model.addAttribute("accountList", accountList);
		}

		return "account/list";
	} // end of listPage()

	/**
	 * 출금 페이지 요청 주소: localhost:8080/account/withdrawal
	 * 
	 * @return
	 */
	@GetMapping("/withdrawal")
	public String withdrawalPage() {
		// 1. 인증검사
		User principal = (User) session.getAttribute(Define.PRINCIPAL);
		if (principal == null) {
			throw new UnAuthorizedException(Define.NOT_AN_AUTHENTICATED_USER, HttpStatus.UNAUTHORIZED);
		}
		return "account/withdrawal";
	}

	/**
	 * 출금 기능
	 * 
	 * @return
	 */
	@PostMapping("/withdrawal")
	public String withdrawalProc(WithdrawalDTO dto) {

		// 1. 인증검사
		User principal = (User) session.getAttribute(Define.PRINCIPAL);

		if (principal == null) { // 유저 세션 값이 null 인 경우
			throw new UnAuthorizedException(Define.NOT_AN_AUTHENTICATED_USER, HttpStatus.BAD_REQUEST);
		}

		if (dto.getAmount().longValue() < 0) {
			throw new DataDeliveryException(Define.ENTER_YOUR_BALANCE, HttpStatus.BAD_REQUEST);
		}

		if (dto.getWAccountNumber() == null) {
			throw new DataDeliveryException(Define.ENTER_YOUR_ACCOUNT_NUMBER, HttpStatus.BAD_REQUEST);
		}

		if (dto.getWAccountPassword() == null || dto.getWAccountPassword().isEmpty()) {
			throw new DataDeliveryException(Define.ENTER_YOUR_PASSWORD, HttpStatus.BAD_REQUEST);
		}

		accountService.updateAccountWithdraw(dto, principal.getId());

		return "redirect:/account/list";
	}

	/**
	 * 입금 페이지 요청
	 * 
	 * @return
	 */
	@GetMapping("/deposit")
	public String depositPage(DepositDTO dto) {
		// 1. 인증검사
		User principal = (User) session.getAttribute(Define.PRINCIPAL);

		if (principal == null) {
			throw new UnAuthorizedException(Define.NOT_AN_AUTHENTICATED_USER, HttpStatus.UNAUTHORIZED);
		}

		return "account/deposit";
	}

	/**
	 * 입금 페이지 요청
	 * 
	 * @param dto
	 * @return
	 */
	@PostMapping("/deposit")
	public String depositProc(DepositDTO dto) {

		User principal = (User) session.getAttribute(Define.PRINCIPAL);

		if (principal == null) { // 유저 세션 정보가 NULL 인 경우
			throw new UnAuthorizedException(Define.NOT_AN_AUTHENTICATED_USER, HttpStatus.UNAUTHORIZED);
		}

		if (dto.getAmount() == null || dto.getAmount().longValue() <= 0) { // 입금 금액이 NULL 인 경우 또는 입금액이 0 인 경우
			throw new DataDeliveryException(Define.ENTER_YOUR_BALANCE, HttpStatus.BAD_REQUEST);
		}

		if (dto.getDAccountNumber() == null || dto.getDAccountNumber().isEmpty()) { // 입금 계좌 번호가 NULL 인 경우
			throw new DataDeliveryException(Define.ENTER_YOUR_ACCOUNT_NUMBER, HttpStatus.BAD_REQUEST);
		}

		accountService.updateAccountDeposit(dto, principal.getId());

		return "redirect:/account/list";
	}

	/**
	 * 계좌 이체 화면 요청
	 * 
	 * @return transfer.jsp
	 */
	@GetMapping("/transfer")
	public String transferPage() {
		// 1. 인증 검사(테스트 시 인증검사 주석 후 화면 확인해 볼 수 있습니다)
		User principal = (User) session.getAttribute(Define.PRINCIPAL); // 다운 캐스팅
		if (principal == null) {
			throw new UnAuthorizedException(Define.ENTER_YOUR_LOGIN, HttpStatus.UNAUTHORIZED);
		}
		return "account/transfer";
	}

	/**
	 * 이체 페이지 요청 계좌 이체 화면 요청
	 * 
	 * @return
	 */
	@PostMapping("/transfer")
	public String transfer(TransferDTO dto) {

		User principal = (User) session.getAttribute(Define.PRINCIPAL);
		// 1. 인증 검사
		if (principal == null) {
			throw new DataDeliveryException(Define.NOT_AN_AUTHENTICATED_USER, HttpStatus.BAD_REQUEST);
		}
		// 2. 유효성 검사
		if (dto.getAmount() == null || dto.getAmount().longValue() <= 0) {
			throw new DataDeliveryException(Define.D_BALANCE_VALUE, HttpStatus.BAD_REQUEST);
		}
		// 2. 유효성 검사
		if (dto.getWAccountNumber() == null || dto.getWAccountNumber().isEmpty()) {
			throw new DataDeliveryException("출금하실 계좌번호를 입력해주세요.", HttpStatus.BAD_REQUEST);
		}
		// 2. 유효성 검사
		if (dto.getDAccountNumber() == null || dto.getDAccountNumber().isEmpty()) {
			throw new DataDeliveryException("이체하실 계좌번호를 입력해주세요", HttpStatus.BAD_REQUEST);
		}
		// 2. 유효성 검사
		if (dto.getPassword() == null || dto.getPassword().isEmpty()) {
			throw new DataDeliveryException(Define.ENTER_YOUR_PASSWORD, HttpStatus.BAD_REQUEST);
		}

		// 서비스 호출
		accountService.updateAccountTransfer(dto, principal.getId());

		return "redirect:/account/list";
	} // end of transfer()

	/**
	 * 계좌 상세 보기 페이지 
	 * 주소 설계 : http://localhost:8080/account/detail/1?type=all, deposit, withdraw
	 * @return
	 */
	@GetMapping("/detail/{accountId}")
	public String detail(@PathVariable(name = "accountId") Integer accountId, @RequestParam(required = false, name ="type") String type,
			@RequestParam(name = "page", defaultValue = "1") int page,
			@RequestParam(name = "size", defaultValue = "2") int size,
			Model model) {		
		
		// 인증검사  
		User principal = (User) session.getAttribute(Define.PRINCIPAL); // 다운 캐스팅
		if (principal == null) {
			throw new UnAuthorizedException(Define.ENTER_YOUR_LOGIN, HttpStatus.UNAUTHORIZED);
		}

		// 유효성 검사 
		List<String> validTypes = Arrays.asList("all", "deposit", "withdrawal");

		if(!validTypes.contains(type)) {
			throw new DataDeliveryException("유효하지 않은 접근 입니다", HttpStatus.BAD_REQUEST);
		}
		
		// 페이지 개수를 계산하기 위해서 총 페이지 수를 계산해주어야 한다.
		int totalPages = accountService.countHistoryByAccountIdAndType(type, accountId);
		System.out.println("totalPages : " + totalPages);

		Account account = accountService.readAccountById(accountId);
		System.out.println("account : " + account);
		
		List<HistoryAccount> historyList = accountService.readHistoryByAccountId(type, accountId, page, size);
		System.out.println("type : " + type);
		System.out.println("accountId : " + accountId);
		System.out.println("page : " + page);
		System.out.println("size : " + size);


		model.addAttribute("account", account);
		model.addAttribute("historyList", historyList);
		model.addAttribute("currentPage", page);
		model.addAttribute("totalPages", totalPages);
		model.addAttribute("type", type);
		model.addAttribute("size", size);
		return "account/detail";
	}

}
