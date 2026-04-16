package fr.utc.miage.transpitrack.model.jpa;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertFalse;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import fr.utc.miage.transpitrack.model.Friendship;
import fr.utc.miage.transpitrack.model.User;
import fr.utc.miage.transpitrack.model.enumer.FriendshipStatus;

@ExtendWith(MockitoExtension.class)
class FriendshipServiceTest {

    @Mock
    private FriendshipRepository friendshipRepository;

    @InjectMocks
    private FriendshipService friendshipService;

    @Test
    void updateFriendshipShouldReturnSavedFriendship() {
        Friendship friendship = new Friendship();
        when(friendshipRepository.save(friendship)).thenReturn(friendship);

        Friendship result = friendshipService.updateFriendship(friendship);

        assertEquals(friendship, result);
        verify(friendshipRepository).save(friendship);
    }

    @Test
    void deleteFriendshipShouldCallRepository() {
        friendshipService.deleteFriendship(1L);

        verify(friendshipRepository).deleteById(1L);
    }

    @Test
    void getMyFriendshipsShouldReturnAcceptedFriendships() {
        Friendship f1 = new Friendship();
        Friendship f2 = new Friendship();

        when(friendshipRepository.findByStatusAndRequesterId(FriendshipStatus.ACCEPTED, 1L))
                .thenReturn(List.of(f1));
        when(friendshipRepository.findByStatusAndReceiverId(FriendshipStatus.ACCEPTED, 1L))
                .thenReturn(List.of(f2));

        List<Friendship> result = friendshipService.getMyFriendships(1L);

        assertEquals(2, result.size());
        verify(friendshipRepository).findByStatusAndRequesterId(FriendshipStatus.ACCEPTED, 1L);
        verify(friendshipRepository).findByStatusAndReceiverId(FriendshipStatus.ACCEPTED, 1L);
    }

    @Test
    void getMyPendingFriendshipsShouldReturnPendingList() {
        Friendship f1 = new Friendship();

        when(friendshipRepository.findByStatusAndReceiverId(FriendshipStatus.PENDING, 1L))
                .thenReturn(List.of(f1));

        List<Friendship> result = friendshipService.getMyPendingFriendships(1L);

        assertEquals(1, result.size());
        verify(friendshipRepository).findByStatusAndReceiverId(FriendshipStatus.PENDING, 1L);
    }

    @Test
    void requestOrFriendshipExistsShouldReturnTrueWhenExistsInSameDirection() {
        when(friendshipRepository.existsByRequesterIdAndReceiverId(1L, 2L))
                .thenReturn(true);
        when(friendshipRepository.existsByRequesterIdAndReceiverId(2L, 1L))
                .thenReturn(false);

        boolean result = friendshipService.requestOrFriendshipExists(1L, 2L);

        assertTrue(result);
        verify(friendshipRepository).existsByRequesterIdAndReceiverId(1L, 2L);
        verify(friendshipRepository).existsByRequesterIdAndReceiverId(2L, 1L);
    }

    @Test
    void requestOrFriendshipExistsShouldReturnTrueWhenExistsInReverseDirection() {
        when(friendshipRepository.existsByRequesterIdAndReceiverId(1L, 2L))
                .thenReturn(false);
        when(friendshipRepository.existsByRequesterIdAndReceiverId(2L, 1L))
                .thenReturn(true);

        boolean result = friendshipService.requestOrFriendshipExists(1L, 2L);

        assertTrue(result);
    }

    @Test
    void requestOrFriendshipExistsShouldReturnFalseWhenNoRelationshipExists() {
        when(friendshipRepository.existsByRequesterIdAndReceiverId(1L, 2L))
                .thenReturn(false);
        when(friendshipRepository.existsByRequesterIdAndReceiverId(2L, 1L))
                .thenReturn(false);

        boolean result = friendshipService.requestOrFriendshipExists(1L, 2L);

        assertFalse(result);
    }

    @Test
    void sendFriendRequestShouldCreateAndSaveFriendship() {
        User requester = new User();
        User receiver = new User();

        Friendship saved = new Friendship(requester, receiver);

        when(friendshipRepository.save(org.mockito.ArgumentMatchers.any(Friendship.class)))
                .thenReturn(saved);

        Friendship result = friendshipService.sendFriendRequest(requester, receiver);

        assertEquals(receiver, result.getReceiver());

        verify(friendshipRepository)
                .save(org.mockito.ArgumentMatchers.any(Friendship.class));
    }

    @Test
    void sendFriendRequestShouldReturnNullWhenRelationshipAlreadyExists() {

        User requester = mock(User.class);
        User receiver = mock(User.class);

        when(requester.getId()).thenReturn(1L);
        when(receiver.getId()).thenReturn(2L);

        when(friendshipRepository.existsByRequesterIdAndReceiverId(1L, 2L)).thenReturn(true);

        Friendship result = friendshipService.sendFriendRequest(requester, receiver);

        assertEquals(null, result);

        verify(friendshipRepository).existsByRequesterIdAndReceiverId(1L, 2L);
        verify(friendshipRepository, never()).save(any());
    }

    @Test
    void getFriendshipByIdShouldReturnFriendshipWhenExists() {

        Friendship friendship = new Friendship();

        when(friendshipRepository.findById(1L))
                .thenReturn(java.util.Optional.of(friendship));

        Friendship result = friendshipService.getFriendshipById(1L);

        assertEquals(friendship, result);
        verify(friendshipRepository).findById(1L);
    }

    @Test
    void getFriendshipByIdShouldReturnNullWhenNotFound() {

        when(friendshipRepository.findById(1L))
                .thenReturn(java.util.Optional.empty());

        Friendship result = friendshipService.getFriendshipById(1L);

        assertEquals(null, result);
    }

    @Test
    void acceptFriendRequestShouldUpdateStatusAndSave() {

        Friendship friendship = mock(Friendship.class);

        friendshipService.acceptFriendRequest(friendship);

        verify(friendship).setStatus(FriendshipStatus.ACCEPTED);
        verify(friendshipRepository).save(friendship);
    }

    @Test
    void rejectFriendRequestShouldDeleteFriendship() {

        Friendship friendship = new Friendship();

        friendshipService.rejectFriendRequest(friendship);

        verify(friendshipRepository).delete(friendship);
    }

    @Test
    void shouldReturnSentPendingFriendships() {

        Friendship friendship = mock(Friendship.class);
        List<Friendship> expected = List.of(friendship);

        when(friendshipRepository.findByStatusAndRequesterId(FriendshipStatus.PENDING, 1L))
                .thenReturn(expected);

        List<Friendship> result = friendshipService.getMySentPendingFriendships(1L);

        assertEquals(expected, result);

        verify(friendshipRepository)
                .findByStatusAndRequesterId(FriendshipStatus.PENDING, 1L);
    }
}
