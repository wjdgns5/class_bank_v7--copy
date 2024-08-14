package com.tenco.bank.service;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.tenco.bank.dto.DepositDTO;
import com.tenco.bank.dto.SaveDTO;
import com.tenco.bank.dto.TransferDTO;
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
		
		// update 처리 
		accountRepository.updateById(accountEntity);
		
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
	
	/**
	 * 1. 계좌 존재 여부를 확인
	 * 2. 본인 계좌 여부를 확인 -- 객체 상태값에서 비교
	 * 3. 입금 처리 -- update
	 * 4. 거래 내역 등록 -- insert(history)
	 * @param dto
	 * @param id
	 */
	
	@Transactional
	public void updateAccountDeposit(DepositDTO dto, Integer principalId) {
		
		// 1.  계좌 존재 여부를 확인
		Account accountEntity = accountRepository.findByNumber(dto.getDAccountNumber());
		
		if(accountEntity == null) {
			throw new DataDeliveryException(Define.NOT_EXIST_ACCOUNT, HttpStatus.BAD_REQUEST);
		}
		
		// 2. 본인 계좌 여부를 확인
		accountEntity.checkOwner(principalId); // 
		accountEntity.deposit(dto.getAmount()); // 입금 처리 
		// 3. 입금 처리
		accountEntity.checkBalance(dto.getAmount());
		
		// 4. 입금 처리 후 계좌 잔액 정리
		accountRepository.updateById(accountEntity);
		
		// 4. 거래 내역 등록
		// history 빌더에 값을 넣는다.
		History history = History.builder()
								 .amount(dto.getAmount())
								 .dAccountId(accountEntity.getId())
								 .dBalance(accountEntity.getBalance())
								 .wAccountId(null)
								 .wBalance(null)
								 .build();
		// insert 쿼리에 history 값을 넣는다.
		int rowResultCount = historyRepository.insert(history); 
		// rowResultCount 가 1이 되면 성공 1이 아니라면 실패라는 뜻이다.
		if(rowResultCount != 1) {
			throw new DataDeliveryException(Define.FAILED_PROCESSING, HttpStatus.INTERNAL_SERVER_ERROR);
		}	
	} // end of updateAccountDeposit()

	// 이체 기능 만들기
	/**
	 * // 이체 기능 만들기
    // 1. 출금 계좌 존재여부 확인 -- select (객체 리턴 받은 상태)
    // 2. 입금 계좌 존재여부 확인 -- select (객체 리턴 받은 상태)
    // 3. 출금 계좌 본인 소유 확인 -- 객체 상태값과 세션 아이디(ID) 비교
    // 4. 출금 계좌 비밀 번호 확인 -- 객체 상태값과 dto 비밀번호 비교
    // 5. 출금 계좌 잔액 여부 확인 -- 객체 상태값 확인, dto와 비교
    // 6. 입금 계좌 객체 상태값 변경 처리 (거래금액 증가처리)
    // 7. 입금 계좌 -- update 처리 
    // 8. 출금 계좌 객체 상태값 변경 처리 (잔액 - 거래금액)
    // 9. 출금 계좌 -- update 처리 
    // 10. 거래 내역 등록 처리
    // 11. 트랜잭션 처리
	 * @param dto
	 * @param id
	 */
	public void updateAccountTransfer(TransferDTO dto, Integer principalId) {
		
		// 출금 계좌 정보 조회
		Account withdrawAccountEntity = accountRepository.findByNumber(dto.getWAccountNumber());
		System.out.println("출금할 계좌 정보 조회 : " + withdrawAccountEntity); 
		// 입금 계좌 정보 조회
		Account depositAccountEntity = accountRepository.findByNumber(dto.getDAccountNumber());
		System.out.println("입금할 계좌 정보 조회 : " + depositAccountEntity);
		
		if (withdrawAccountEntity == null) { // 출금 계좌 정보가 null 이면
			throw new DataDeliveryException(Define.NOT_EXIST_ACCOUNT, HttpStatus.INTERNAL_SERVER_ERROR);
		}
		
		if (depositAccountEntity == null) { // // 입금 계좌 정보가 null 이면
			throw new DataDeliveryException("상대방의 계좌 번호가 없습니다.", HttpStatus.INTERNAL_SERVER_ERROR);
		}
		
		// 출금하는 사용자의 정보를 확인하는 작업
		withdrawAccountEntity.checkOwner(principalId); // 계좌 소유자 확인
		withdrawAccountEntity.checkPassword(dto.getPassword()); // 패스워드 체크 기능
		withdrawAccountEntity.checkBalance(dto.getAmount()); // 잔액 여부 확인 기능
		withdrawAccountEntity.withdraw(dto.getAmount()); // 출금 기능
		depositAccountEntity.deposit(dto.getAmount()); // 입금 기능
		
		
		int resultRowCountWithdraw = accountRepository.updateById(withdrawAccountEntity); // 새로 갱신
    	int resultRowCountDeposit = accountRepository.updateById(depositAccountEntity); // 새로 갱신
		
    	if(resultRowCountWithdraw != 1) { // resultRowCountWithdraw 출금 성공여부 확인
			throw new DataDeliveryException(Define.NOT_EXIST_ACCOUNT, HttpStatus.INTERNAL_SERVER_ERROR);
		}
		
		if(resultRowCountDeposit != 1) { // resultRowCountDeposit 입금 성공여부 확인
			throw new DataDeliveryException(Define.NOT_EXIST_ACCOUNT, HttpStatus.INTERNAL_SERVER_ERROR);
		}
	
		// historyDTO에 builder를 통해서 값을 넣어서 추가해준다.
		History history = History.builder()
								 .amount(dto.getAmount())
								 .wAccountId(withdrawAccountEntity.getId())
								 .dAccountId(depositAccountEntity.getId())
								 .wBalance(depositAccountEntity.getBalance())
								 .dBalance(withdrawAccountEntity.getBalance())
								 .build();
		
		// 이체 기록에 추가
		int resultRowCountHistory = historyRepository.insert(history);
		if(resultRowCountHistory != 1) { // 쿼리를 추가하게 되면 1이 올라간다 아니라면 실패를 의미한다.
			throw new DataDeliveryException(Define.FAILED_PROCESSING, HttpStatus.INTERNAL_SERVER_ERROR);
		}
		
	} // end of updateAccountTransfer()

}
