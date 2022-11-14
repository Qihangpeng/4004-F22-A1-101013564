@Tag
  @singleRoll
  Feature: player plays a single round and then score
    Scenario Outline: player roll eight dice, score
      Given player roll eight dice <0> <1> <2> <3> <4> <5> <6> <7>
      And fortune card is <fc>
      When player end turn
      Then player score <score>
      And player is <dead>
      Examples:
        | 0 | 1 | 2 | 3 | 4 | 5 | 6 | 7 | fc |score  | dead |
        |3  |3  |3  |4  |4  |4  |4  |4  |4   |0      |1     |

    @rerollOnce
    Scenario Outline: player roll eight dice, re-roll once, score
      Given player roll eight dice <0> <1> <2> <3> <4> <5> <6> <7>
      And fortune card is <fc>
      When re-roll dice <index>
      And got new dice <dice>
      Then player score <score>
      And player is <dead>
      Examples:
        | 0 | 1 | 2 | 3 | 4 | 5 | 6 | 7 | index  | dice   | fc |score  | dead |
        |3  |2  |2  |2  |2  |4  |4  |4  |"5,6,7" |"3,3,4" |4   |0      |1     |
        |3  |3  |2  |2  |2  |2  |4  |4  |"6,7"   |"3,4"   |4   |0      |1     |




