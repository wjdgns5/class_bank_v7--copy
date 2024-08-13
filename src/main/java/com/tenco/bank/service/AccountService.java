package com.tenco.bank.service;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.tenco.bank.dto.SaveDTO;
import com.tenco.bank.dto.WithdrawalDTO;
import com.tenco.bank.handler.exception.DataDeliveryException;
import com.tenco.bank.handler.exception.RedirectException;
import com.tenco.bank.repository.interfaces.AccountRepository;
import com.tenco.bank.repository.interfaces.HistoryRepository;
import com.tenco.bank.repository.model.Account;
import com.tenco.bank.repository.model.History;
import com.tenco.bank.utils.Define;

@Service
public class AccountService {
	
	
	private final AccountRepository accountRepository;
	private final HistoryRepository historyRepository;
	
	public AccountService(AccountRepository accountRepository, HistoryRepository historyRepository) {
		this.accountRepository = accountRepository;
		this.historyRepository = historyRepository;
	}

	/**
	 * 계좌 생성 기능
	 * @param dto
	 * @param principalId
	 */
	public void createAccount(SaveDTO dto, Integer principalId) {
		int result = 0;
		
		try {
			result = accountRepository.insert(dto.toAccount(principalId));
			
		} catch(DataDeliveryException e) {
			throw new DataDeliveryException("잘못된 요청입니다.", HttpStatus.INTERNAL_SERVER_ERROR);

		} catch(Exception e) {
			throw new RedirectException("알 수 없는 오류 입니다.", HttpStatus.SERVICE_UNAVAILABLE);
		}
		
		if(result == 0) {
			throw new DataDeliveryException("정상 처리 되지 않았습니다.", HttpStatus.INTERNAL_SERVER_ERROR);
		}
		// TODO - DTO 값 확인
		System.out.println("SaveDTO : " + dto.toString());
	}

	/**
	 * 
	 * @param id
	 * @return
	 */
	public List<Account> readAccountListByUserId(Integer userId) {
		
		List<Account> accountListEntity = null;
		
		try {
			accountListEntity = accountRepository.findByUserId(userId);
		} catch (DataDeliveryException e) {
			throw new DataDeliveryException("잘못된 처리 입니다.", HttpStatus.INTERNAL_SERVER_ERROR);
		} catch(Exception e) {
			throw new RedirectException("알 수 없는 오류", HttpStatus.SERVICE_UNAVAILABLE);
		}
	
		return accountListEntity;
	}

	// 한번에 모든 기능을 생각 힘듬
	// 1. 계좌 존재 여부를 확인 -- select 
	// 2. 본인 계좌 여부를 확인 -- 객체 상태값에서 비교 
	// 3. 계좌 비번 확인 --        객체 상태값에서 일치 여부 확인
	// 4. 잔액 여부 확인 --        객체 상태값에서 확인 
	// 5. 출금 처리      --        update 
	// 6. 거래 내역 등록 --        insert(history) 
	// 7. 트랜잭션 처리 
	
	@Transactional
	public void updateAccountWithdraw(WithdrawalDTO dto, Integer principalId) {
		// 출금 계좌번호로 account_tb로 회원을 조회한다.
		Account accountEntity = accountRepository.findByNumber(dto.getWAccountNumber()); 
		
		if(accountEntity == null) {
			throw new DataDeliveryException(Define.NOT_EXIST_ACCOUNT, HttpStatus.BAD_REQUEST);
		}
		
		// 2 - 계좌 소유주 확인
		accountEntity.checkOwner(principalId);
		// 3 - 패스워드 체크 기능
		accountEntity.checkPassword(dto.getWAccountPassword());
		// 4 - 잔액 여부 확인 기능
		accountEntity.checkBalance(dto.getAmount());
		// 5 - 출금 기능
		// accoutEntity 객체의 잔액을 변경하고 업데이트 처리해야 한다. 
		accountEntity.withdraw(dto.getAmount());
		
		// 6 - 거래 내역 등록 
		History history = new History();
		history.setAmount(dto.getAmount()); // 거래금액
		history.setWBalance(accountEntity.getBalance()); // 계좌잔액 
		history.setDBalance(null); // 입금 금액
		history.setWAccountId(accountEntity.getId()); // 출금 한 아이디
		history.setDAccountId(null); // 입금 받은 아이디
		
		int rowResultCount = historyRepository.insert(history);
		if(rowResultCount != 1) {
			// 1 이 안나오면 쿼리가 실패한 것이다.
			throw new DataDeliveryException(Define.FAILED_PROCESSING, HttpStatus.INTERNAL_SERVER_ERROR);
		}
		
	}

}
