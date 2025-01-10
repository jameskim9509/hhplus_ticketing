package kr.hhplus.be.server.infrastructure.core.waiting_queue;

import jakarta.persistence.LockModeType;
import kr.hhplus.be.server.domain.waiting_queue.WaitingQueue;
import kr.hhplus.be.server.domain.waiting_queue.type.WaitingQueueStatus;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface WaitingQueueJpaRepository extends JpaRepository<WaitingQueue, Long> {
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("select w from WaitingQueue w where w.user.uuid=:user_uuid")
    public List<WaitingQueue> findAllByUuidWithLock(@Param("user_uuid") String uuid);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("select w from WaitingQueue w where w.status=:status")
    public List<WaitingQueue> findAllByStatusWithLock(@Param("status") WaitingQueueStatus status);

    public List<WaitingQueue> findAllByStatus(WaitingQueueStatus status);

    @Query("SELECT w FROM WaitingQueue w WHERE w.status=:status ORDER BY w.id ASC")
    public List<WaitingQueue> findAllByStatusASCWithPage(@Param("status") WaitingQueueStatus status, Pageable pageable);

    @Query("SELECT w FROM WaitingQueue w WHERE w.status=:status ORDER BY w.id DESC")
    public List<WaitingQueue> findAllByStatusDESCWithPage(@Param("status") WaitingQueueStatus status, Pageable pageable);
}
