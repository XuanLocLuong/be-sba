package sba301.fe.edu.vn.besba.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import sba301.fe.edu.vn.besba.entity.Room;

@Repository
public interface RoomRepository extends JpaRepository<Room, Integer> {
    boolean existsByName(String name);
}