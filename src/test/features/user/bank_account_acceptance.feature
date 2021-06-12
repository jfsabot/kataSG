Feature: User management

    Scenario: US1 Deposit
        When I check account position as user 'John Doe', I expect
            | ownerName | position |
            | John Doe  |     3826 |
        #Then as user 'John Doe' I make a deposit of 100 euros
