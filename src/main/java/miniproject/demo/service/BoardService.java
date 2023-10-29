package miniproject.demo.service;

import lombok.RequiredArgsConstructor;
import miniproject.demo.dao.BoardDao;
import miniproject.demo.dto.Board;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class BoardService {
    private final BoardDao boardDao;
    @Transactional
    public void addBoard(int userid, String title, String content) {
        boardDao.addBoard(userid, title, content);

    }

    @Transactional(readOnly = true) // select할때만 성능향상목적
    public int getTotalCount() {
        return boardDao.getTotalCount();

    }

    @Transactional(readOnly = true) //  select할때만 성능향상목적
    public List<Board> getBoards(int page) {
        return boardDao.getBoards(page);
    }

    @Transactional
    public Board getBoard(int boardId) {
        return getBoard(boardId, true);
    }

    @Transactional
    public Board getBoard(int boardId, boolean updateViewCnt){
        //updateViewCnt가 true면 글의 조회수 증가, false면 글의 조회수 증가 x
        Board board = boardDao.getBoard(boardId);
        if(updateViewCnt) {
            boardDao.updateViewCnt(boardId);
        }
        return board;
    }

    @Transactional
    public void deleteBoard(int userId, int boardId) {
        Board board = boardDao.getBoard(boardId);
        if (board.getUserId() == userId) {
            boardDao.deleteBoard(boardId);
        }
    }

    @Transactional
    public void deleteBoard(int boardId) {
        boardDao.deleteBoard(boardId);
    }

    @Transactional
    public void updateBoard(int boardId, String title, String content) {
        boardDao.updateBoard(boardId, title, content);
    }
}