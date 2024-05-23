package ru.practicum.ewmStatsServer.config;

import lombok.AllArgsConstructor;
import org.modelmapper.Converter;
import org.modelmapper.ModelMapper;
import org.modelmapper.spi.MappingContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import ru.practicum.ewmStatsDto.Constants;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Configuration
@AllArgsConstructor
public class ModelMapperConfig {
    @Bean
    public ModelMapper modelMapper() {
        ModelMapper mapper = new ModelMapper();
        mapper.addConverter(new Converter<LocalDateTime, String>() {
            public String convert(MappingContext<LocalDateTime, String> context) {
                DateTimeFormatter dtFormatter = DateTimeFormatter.ofPattern(Constants.DATE_FORMAT);
                return context.getSource() == null ? null : dtFormatter.format(context.getSource());
            }
        });
        return mapper;
    }
}
