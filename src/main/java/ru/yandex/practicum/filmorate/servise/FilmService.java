package ru.yandex.practicum.filmorate.servise;

import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class FilmService {


    private final HashMap<Integer, Set<Integer>> filmLikesStorage = new HashMap<>();

    private Map<Integer, Long> prioritizedFilms = new HashMap<>();

    public void addLike(Integer userId, Integer filmId) {
        filmLikesStorage.get(filmId).add(userId);//проверка повторного лайка не нужна, ибо Set
        prioritizedFilmsUpdater();
    }

    public void removeLike(Integer userId, Integer filmId) {
        filmLikesStorage.get(filmId).remove(userId);
        prioritizedFilmsUpdater();
    }

    public List<Integer> getPopularFilms(Integer counter) {
        if (!prioritizedFilms.keySet().isEmpty()) {
            if (prioritizedFilms.keySet().size() > counter) {
                return prioritizedFilms.keySet().stream().
                        limit(counter).collect(Collectors.toList());
            } else
                return new LinkedList<>(prioritizedFilms.keySet());
        } else return Collections.emptyList();
    }

    /* Думаю вскоре этот метод пригодится, а пока пусть побудет в сером цвете)
    public Long getLikesCounter(Integer id) {
        return prioritizedFilms.get(id);
    }*/

    public void addFilmId(Integer filmId) {
        filmLikesStorage.put(filmId, new HashSet<>());
        prioritizedFilmsUpdater();
    }

    private void prioritizedFilmsUpdater() {
        if (!prioritizedFilms.isEmpty()) {
            prioritizedFilms.clear();
        }
        for (Integer filmId : filmLikesStorage.keySet()) {
            long numOfLikes = filmLikesStorage.get(filmId).size();
            prioritizedFilms.put(filmId, numOfLikes);
        }
        prioritizedFilms = sorter(prioritizedFilms);
    }

    private Map<Integer, Long> sorter(Map<Integer, Long> unsortedMap) {
        return unsortedMap.entrySet().stream()
                .sorted(Map.Entry.comparingByValue(Collections.reverseOrder()))
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue,
                        (a, b) -> {
                            throw new AssertionError();
                        }, LinkedHashMap::new
                ));
    }
}