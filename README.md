# Bank Asia Duty Roster (TOC + Application Management)

One web application with two modules. November 2026 data from the published PDFs is loaded on first start.

| Module | Pages | Contents |
|---|---|---|
| **TOC** (Technology Operations Center) | `/roster`, `/employees` | Morning 10:00 am-05:00 pm, Evening 02:30 pm-09:30 pm, Night 09:30 pm-10:00 am; team; escalation matrix; contact numbers |
| **Application Management** | `/am/roster`, `/am/team`, `/am/branches`, `/am/departments` | Roster duty 02:00-09:00 PM, additional duty 06:00-06:30 PM, replacement 10:00 AM-02:00 PM; holidays, Saturday duty, month-end "all present"; team with mobile and IP ext.; branch-wise primary/secondary officers with search; corporate office departments by officer; escalation matrix |

## Stack
Java 17, Spring Boot 3.3, Spring MVC + Thymeleaf, Spring Data JPA, Spring Security, H2 (default) / PostgreSQL.

## Run
```bash
mvn spring-boot:run          # http://localhost:8080
mvn test                     # unit/integration tests
mvn clean package && java -jar target/toc-roster-1.0.0.jar
```
PostgreSQL: `java -jar target/toc-roster-1.0.0.jar --spring.profiles.active=postgres`
(env: `DB_URL`, `DB_USER`, `DB_PASSWORD`).

## Access
- **Everyone** (no login): view the monthly roster, "on duty now", team directory, export/print.
- **Admin**: click **Admin login** in the header, sign in with username and password to edit rosters and manage the team.

Default admin: `admin` / `ChangeMe#2026`. Change with `TOC_ADMIN_USER` and `TOC_ADMIN_PASSWORD`.

## Features
- Monthly roster table matching the PDF layout; Friday rows highlighted, today outlined, month navigation.
- "On duty now" panel, aware of the night shift crossing midnight (Asia/Dhaka time zone).
- Admin: edit any day (multi-select per shift), add/edit/deactivate/remove team members.
  Members with roster history are deactivated, never deleted.
- CSV export and print-friendly stylesheet.
- Escalation matrix and contact numbers (`application.properties`).

## Application Management module
- **Roster**: each date is a *Working day*, *Holiday*, *Saturday duty* (one officer) or *All present* (month end). Admin edits a day with a day-type selector plus checkboxes per slot. "On duty now" follows the slot times.
- **Team**: members with mobile, IP extension and ERA flag. Members used in rosters, branches or departments are deactivated instead of deleted.
- **Branches**: 10 groups, each with a primary and secondary officer; public search by branch code or name. Admin can change officers and add/remove branches.
- **Departments**: corporate office departments grouped by assigned officer; admin can assign or remove.

## Structure
```
com.bankasia.toc
  domain, repository, service, web   TOC module
  am/{domain,repository,service,web} Application Management module
  config/                            SecurityConfig, DataSeeder (TOC), AmDataSeeder (AM)
resources/templates                  Thymeleaf pages (am-*.html for Application Management)
resources/static/css/app.css
```

## Next steps for production
Replace in-memory users with LDAP/SSO or a user table, add Flyway migrations and set
`spring.jpa.hibernate.ddl-auto=validate`, and add a "copy previous month" generator if rosters follow a rotation.
