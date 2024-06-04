package ru.practicum.ewmServer.config;

import lombok.AllArgsConstructor;
import org.modelmapper.Converter;
import org.modelmapper.ModelMapper;
import org.modelmapper.spi.MappingContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import ru.practicum.ewmServer.compilations.dto.CompilationDto;
import ru.practicum.ewmServer.events.dto.EventFullDto;
import ru.practicum.ewmServer.events.dto.EventShortDto;
import ru.practicum.ewmServer.events.model.EventModel;
import ru.practicum.ewmServer.users.model.UserModel;
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

        mapper.addConverter(new Converter<CompilationDto, Long>() {
            public Long convert(MappingContext<CompilationDto, Long> context) {
                return context.getSource() == null ? null : context.getSource().getId();
            }
        });

        mapper.addConverter(new Converter<EventShortDto, Long>() {
            public Long convert(MappingContext<EventShortDto, Long> context) {
                return context.getSource() == null ? null : context.getSource().getId();
            }
        });

        mapper.addConverter(new Converter<EventFullDto, Long>() {
            public Long convert(MappingContext<EventFullDto, Long> context) {
                return context.getSource() == null ? null : context.getSource().getId();
            }
        });

        mapper.addConverter(new Converter<UserModel, Long>() {
            public Long convert(MappingContext<UserModel, Long> context) {
                return context.getSource() == null ? null : context.getSource().getId();
            }
        });

        mapper.addConverter(new Converter<EventModel, Long>() {
            public Long convert(MappingContext<EventModel, Long> context) {
                return context.getSource() == null ? null : context.getSource().getId();
            }
        });

        return mapper;
    }


}
