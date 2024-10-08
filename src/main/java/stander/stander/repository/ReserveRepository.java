package stander.stander.repository;

import groovy.util.logging.Slf4j;
import org.springframework.stereotype.Repository;
import stander.stander.model.Entity.Member;
import stander.stander.model.Entity.TimeBoard;

import javax.persistence.EntityManager;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;

@Slf4j
@Repository
public class ReserveRepository implements ReserveInterface{

    private final EntityManager em;

    public ReserveRepository(EntityManager em) {
        this.em = em;
    }

    @Override
    public List<TimeBoard> OneWeek() {
        List<TimeBoard> resultList = em.createQuery("select m from TimeBoard m order by m.date asc", TimeBoard.class)
                .setMaxResults(7)
                .getResultList();
        return resultList;
    }


    @Override
    public void makeTimeBoard(LocalDateTime localDateTime) {
        TimeBoard timeBoard = new TimeBoard(localDateTime);
        em.persist(timeBoard);
//        em.flush();
    }

    public TimeBoard getTimeBoard(LocalDateTime localDateTime){
        try {

            return em.createQuery("select m from TimeBoard m where m.date > :start order by m.date asc", TimeBoard.class)
                    .setParameter("start", localDateTime.minusMinutes(10))
                    .getResultList()
                    .get(0);
        }
        catch(Exception e) {
            System.out.println(e.getMessage());
            return null;
        }
    }

    public void reserveTime(Member member, LocalDateTime dateTime, int start, int end) {

        TimeBoard timeBoard = getTimeBoard(dateTime);
        Map<Integer, Member> times = timeBoard.getTimes();

        for(int i = start; i < end; i++){
            times.put( i, member);
        }
    }

    @Override
    public void cancleTime(LocalDateTime localDateTime, Member member) {
        TimeBoard timeBoard = getTimeBoard(localDateTime);
        Map<Integer, Member> times = timeBoard.getTimes();
        for(int i = 0; i < 24; i++){
            Member findmember = times.get(i);
            if ( findmember != null && times.get(i).getUsername().equals(member.getUsername())){
                times.put(i, null);
            }
        }
    }

    public List<TimeBoard> allTimeBoard() {
        List<TimeBoard> all = em.createQuery("select m from TimeBoard m", TimeBoard.class).getResultList();
        return all;
    }
}
