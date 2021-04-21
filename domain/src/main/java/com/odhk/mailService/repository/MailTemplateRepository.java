package com.odhk.mailService.repository;

import com.odhk.mailService.aggregates.MailTemplate;
import com.odhk.mailService.constant.MailTemplateType;

import java.util.Optional;

public interface MailTemplateRepository {
    public Optional<MailTemplate> findByTemplateType(MailTemplateType type);
    public void save(MailTemplate template);
    public void delete(MailTemplate template);
}
