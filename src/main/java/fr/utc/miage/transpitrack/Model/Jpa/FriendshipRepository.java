package fr.utc.miage.transpitrack.model.jpa;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import fr.utc.miage.transpitrack.model.Friendship;
import fr.utc.miage.transpitrack.model.enumer.FriendshipStatus;

@Repository
public interface FriendshipRepository extends JpaRepository<Friendship, Long> {
    
    List<Friendship> findByRequesterId(Long requesterId);

    List<Friendship> findByReceiverId(Long receiverId);

    List<Friendship> findByStatusAndRequesterId(FriendshipStatus status, Long requesterId);

    List<Friendship> findByStatusAndReceiverId(FriendshipStatus status, Long receiverId);

    Friendship findByRequesterIdAndReceiverId(Long requesterId, Long receiverId);

    boolean existsByRequesterIdAndReceiverId(Long requesterId, Long receiverId);

    boolean existsByRequesterIdAndReceiverIdAndStatus(Long requesterId, Long receiverId, FriendshipStatus status);
}
