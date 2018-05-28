package io.pivotal.pal.tracker;

import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

@Repository
public class InMemoryTimeEntryRepository implements TimeEntryRepository {
    private long lastId = 1L;
    private HashMap<Long, TimeEntry> entries = new HashMap();

    @Override
    public TimeEntry create(TimeEntry timeEntry) {
        TimeEntry createdEntry = new TimeEntry(lastId++, timeEntry.getProjectId(), timeEntry.getUserId(), timeEntry.getDate(), timeEntry.getHours());
        entries.put(createdEntry.getId(), createdEntry);
        return createdEntry;
    }

    @Override
    public TimeEntry find(long id) {
        return entries.get(id);
    }

    @Override
    public List<TimeEntry> list() {
        return new ArrayList<>(entries.values());
    }

    @Override
    public TimeEntry update(long id, TimeEntry timeEntry) {
        TimeEntry updatedEntry = new TimeEntry(id, timeEntry.getProjectId(), timeEntry.getUserId(), timeEntry.getDate(), timeEntry.getHours());
        entries.put(id, updatedEntry);
        return updatedEntry;
    }

    @Override
    public TimeEntry delete(long id) {
        return entries.remove(id);
    }
}
