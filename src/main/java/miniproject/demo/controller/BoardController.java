package miniproject.demo.controller;


import lombok.RequiredArgsConstructor;
import miniproject.demo.dto.Board;
import miniproject.demo.dto.LoginInfo;
import miniproject.demo.service.BoardService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.HttpSession;
import java.util.List;


//http요청을 받아서 응답하는 컴포넌트(스프링부트가 자동으로  Bean으로 생성해버림)
@Controller
@RequiredArgsConstructor
public class BoardController {
    private final BoardService boardService;

    //게시물 목록을 보여줌
    //컨트롤러의 메소드가 리턴하는 문자열은 템플릿 이름임
    //http://localhost:7601/ ---> list라는 이름의 템플릿을 사용(forward)하여 화면에 출력한다.
    //list를 리턴한다는 것은 classpath:/templates/list.html을 사용한다는 거야 디폴트 경로임

    @GetMapping("/")
    public String list(@RequestParam(name="page", defaultValue = "1") int page, HttpSession session, Model model){ // HttpSession, Model은 Spring이 자동으로 넣어준다.
        // 게시물 목록을 읽어온다. 페이징 처리한다.
        LoginInfo loginInfo = (LoginInfo)session.getAttribute("loginInfo");
        model.addAttribute("loginInfo", loginInfo); // 템플릿에게

        int totalCount = boardService.getTotalCount(); // 11
        List<Board> list = boardService.getBoards(page); // page가 1,2,3,4 ....
        int pageCount = totalCount / 10; // 1
        if(totalCount % 10 > 0){ // 나머지가 있을 경우 1page를 추가
            pageCount++;
        }

        int currentPage = page;
        model.addAttribute("list", list);
        model.addAttribute("pageCount", pageCount); // 전체페이지
        model.addAttribute("currentPage", currentPage); // 현제페이지
        return "list";

    }

    // /board?id=3 -> 파라미터 아이디의 값이 3인 것을 받을거야
    // /board?id=2
    @GetMapping("/board")
    public String Board(@RequestParam("boardId") int boardId, Model model){
        System.out.println("boardId : " + boardId);

        //id에 해당하는 게시물을 읽어와야대
        //id에 해당하는 게시물의 조회수도 1 증가시켜봐야지
        Board board = boardService.getBoard(boardId);
        model.addAttribute("board", board);
        return "board";
    }

    //관리자 기능. 모든 글삭제
    //수정하기

    @GetMapping("/writeForm")
    public String writeForm(HttpSession session, Model model){
        //로그인한 사용자만 글을 써야함. 로그인을 하지 않았다면 리스트보기로 강제로 이동시켜버림
        // 그러려면 세션에서 로그인한 정보를 읽어드려야함
        LoginInfo loginInfo = (LoginInfo)session.getAttribute("loginInfo");
        if(loginInfo == null){//세션에 로그인 정보가 없으면 로그인폼으로 리다이렉트
            return "redirect:/loginForm";
        }

        model.addAttribute("loginInfo", loginInfo);

        return "writeForm";
    }
    @PostMapping("/write")
    public String write(
            @RequestParam("title") String title,
            @RequestParam("content") String content,
            HttpSession session
    ){
        LoginInfo loginInfo = (LoginInfo)session.getAttribute("loginInfo");
        if(loginInfo == null){ // 세션에 로그인 정보가 없으면 /loginform으로 redirect
            return "redirect:/loginForm";
        }
        // 로그인한 사용자만 글을 써야한다.
        // 세션에서 로그인한 정보를 읽어들인다. 로그인을 하지 않았다면 리스트보기로 자동 이동 시킨다.
        System.out.println("title : " + title);
        // 로그인 한 회원 정보 + 제목, 내용을 저장한다.System.out.println("content : " + content);

        boardService.addBoard(loginInfo.getUserId(), title, content);

        return "redirect:/"; // 리스트 보기로 리다이렉트한다.
    }

    @GetMapping("/delete")
    public String delete(
            @RequestParam("boardId") int boardId,
            HttpSession session
    ) {
        LoginInfo loginInfo = (LoginInfo) session.getAttribute("loginInfo");
        if (loginInfo == null) { // 세션에 로그인 정보가 없으면 /loginform으로 redirect
            return "redirect:/loginForm";
        }

        // ROLES의 값이 2이면 모든글 삭제가능
        List<String> roles = loginInfo.getRoles();
        if(roles.contains("ROLE_ADMIN")){
            boardService.deleteBoard(boardId);
        }else {
            boardService.deleteBoard(loginInfo.getUserId(), boardId);
        }

        return "redirect:/"; // 리스트 보기로 리다이렉트한다.
    }


    @GetMapping("/updateForm")
    public String updateForm(@RequestParam("boardId") int boardId,
                             Model model,
                             HttpSession session
    ){
        LoginInfo loginInfo = (LoginInfo) session.getAttribute("loginInfo");
        if (loginInfo == null) { // 세션에 로그인 정보가 없으면 /loginform으로 redirect
            return "redirect:/loginForm";
        }
        // boardId에 해당하는 정보를 읽어와서 updateform 템플릿에게 전달한다.
        Board board = boardService.getBoard(boardId, false);
        model.addAttribute("board", board);
        model.addAttribute("loginInfo", loginInfo);
        return "updateForm";
    }

    @PostMapping("/update")
    public String update(@RequestParam("boardId") int boardId,
                         @RequestParam("title") String title,
                         @RequestParam("content") String content,
                         HttpSession session
    ){

        LoginInfo loginInfo = (LoginInfo) session.getAttribute("loginInfo");
        if (loginInfo == null) { // 세션에 로그인 정보가 없으면 /loginform으로 redirect
            return "redirect:/loginForm";
        }

        Board board = boardService.getBoard(boardId, false);
        if(board.getUserId() != loginInfo.getUserId()){
            return "redirect:/board?boardId=" + boardId; // 글보기로 이동한다.
        }
        // boardId에 해당하는 글의 제목과 내용을 수정한다.
        boardService.updateBoard(boardId, title, content);
        return "redirect:/board?boardId=" + boardId; // 수정된 글 보기로 리다이렉트한다.
    }
}