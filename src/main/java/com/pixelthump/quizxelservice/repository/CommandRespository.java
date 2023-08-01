package com.pixelthump.quizxelservice.repository;
import com.pixelthump.quizxelservice.repository.model.command.Command;
import com.pixelthump.quizxelservice.repository.model.CommandId;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CommandRespository extends JpaRepository<Command, CommandId> {

    long deleteByCommandId(CommandId commandId);

    List<Command> findByCommandId_State_SeshCodeOrderByCommandId_TimestampAsc(String seshCode);

}
