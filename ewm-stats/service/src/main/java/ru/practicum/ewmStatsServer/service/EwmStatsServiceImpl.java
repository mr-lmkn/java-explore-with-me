package ru.practicum.ewmStatsServer.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.ewmStatsDto.HitInDto;
import ru.practicum.ewmStatsDto.HitOutDto;
import ru.practicum.ewmStatsDto.StatDto;
import ru.practicum.ewmStatsServer.model.Hit;
import ru.practicum.ewmStatsServer.storage.EvmStatsStorage;
import ru.practicum.ewmStatsServer.storage.EwmStatsRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(propagation = Propagation.REQUIRED, isolation = Isolation.SERIALIZABLE)
public class EwmStatsServiceImpl implements EwmStatsService {
    private final ModelMapper modelMapper;
    private final EwmStatsRepository ewmStatsRepository;
    private final EvmStatsStorage EwmStatsDAO;

    @Override
    @Transactional
    public HitOutDto saveHit(HitInDto hitInDto) {
        Hit hit = modelMapper.map(hitInDto, Hit.class);
        Hit ret = ewmStatsRepository.save(hit);
        return modelMapper.map(ret, HitOutDto.class);
    }

    @Override
    public List<StatDto> getStats(LocalDateTime start, LocalDateTime end
            , Optional<List<String>> urisList, boolean uniqueIp) {
        List<StatDto> ret;
        if (urisList.isPresent()) {
            ret = EwmStatsDAO.getStats(start, end, urisList.get(), false, uniqueIp);
        } else {
            ret = EwmStatsDAO.getStats(start, end, List.of(new String()), true, uniqueIp);
        }
        return ret.stream()
                .map(p -> modelMapper.map(p, StatDto.class))
                .collect(Collectors.toList());
    }

}
