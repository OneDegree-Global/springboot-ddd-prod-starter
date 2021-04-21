package com.odhk.mailService.repository;

import java.util.Optional;

public interface MailListRepository {
    public Optional getList();
    public void update();
    public void delete();
}
