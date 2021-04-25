# 德州扑克规则补充说明文档

德州扑克规则简介参考：[德州扑克规则](http://sports.163.com/special/poker_rule/ )

## 规则补充说明

- 德州扑克是⼀种多⼈扑克游戏，参与⼈数可从2-10⼈

- 玩家在四轮中分别投注，投注⾦额进⼊底池

  - 例如情况如下，最终底池金额为16

    | 玩家 | Pre-flop | flop | turn | river |
    | ---- | -------- | ---- | ---- | ----- |
    | A    | 1        | 1    | 1    | 1     |
    | B    | 1        | 1    | 1    | 1     |
    | C    | 1        | 1    | 1    | 1     |
    | D    | 1        | 1    | 1    | 1     |

- 玩家在行动过程中，有以下选择：

  - 跟注

    ![image-20210425101049094](/Users/fei/workspace/firenze-project/firenze-project-texas-holdem/README.assets/image-20210425101049094.png)

  - 加注

    ![image-20210425101110934](/Users/fei/workspace/firenze-project/firenze-project-texas-holdem/README.assets/image-20210425101110934.png)

  - 弃牌

    ![image-20210425101121951](/Users/fei/workspace/firenze-project/firenze-project-texas-holdem/README.assets/image-20210425101121951.png)

  - 过牌（第一轮不能选择）

    ![image-20210425101132016](/Users/fei/workspace/firenze-project/firenze-project-texas-holdem/README.assets/image-20210425101132016.png)

- 玩家在任⼀轮次中放弃则退出游戏，投⼊底池的资⾦从筹码中扣除

  - 例如每个玩家开始有100筹码，经过如下回合，最终A玩家剩下的筹码为98，B玩家剩下的筹码为99

    | 玩家 | Pre-flop | flop | turn | river |
    | ---- | -------- | ---- | ---- | ----- |
    | A    | 1        | 1    | FOLD | -     |
    | B    | 1        | FOLD | -    | -     |
    | C    | 1        | 1    | 1    | 1     |
    | D    | 1        | 1    | 1    | 1     |

- 在最后⼀轮前，如果只剩⼀位玩家，则该玩家成为赢家，获得底池内所有奖⾦

  - 例如玩家投注情况如下，则A玩家成为本局赢家，共赢得8筹码

    | 玩家 | Pre-flop | flop | turn | river |
    | ---- | -------- | ---- | ---- | ----- |
    | A    | 1        | 1    | 1    | -     |
    | B    | 1        | 1    | FOLD | -     |
    | C    | 1        | 1    | FOLD | -     |
    | D    | 1        | FOLD | -    | -     |
  
- 直到最后⼀轮投注结束，仍未退出的玩家进⼊摊牌阶段

  - 例如如下情况，则A,B,C玩家进入摊牌阶段

    | 玩家 | Pre-flop | flop | turn | river |
    | ---- | -------- | ---- | ---- | ----- |
    | A    | 1        | 1    | 1    | 1     |
    | B    | 1        | 1    | 1    | 1     |
    | C    | 1        | 1    | 1    | 1     |
    | D    | 1        | 1    | FOLD | -     |

- 摊牌阶段，仍未退出的玩家从⼿牌与河牌挑选最⼤的组合，进⾏⽐较

  - 大小比较规则如下

  ![组合大小](http://static.ws.126.net/sports/2014/10/16/201410161826390d1e8.png)

- 如有⼀⼈胜出，则该玩家成为赢家，获得底池内所有奖⾦

  - 如下情况下，结算后A玩家将获得16个筹码

  **Given：**

  | 进入摊牌阶段玩家（持有筹码，投注筹码） | 弃牌玩家（持有筹码，投注筹码） | 当局游戏筹码底池 |
  | -------------------------------------- | ------------------------------ | ---------------- |
  | A(100,4), B(100,4), C(100,4), D(100,4) |                                | 16               |

  **And**：A玩家的组合最大

  **When：**进行筹码结算

  **Then：**
  
  | 玩家 | 持有筹码 |
  | ---- | -------- |
  | A    | 112      |
  | B    | 96       |
  | C    | 96       |
  | D    | 96       |
  


- 如平局，则赢家平分底池内所有奖⾦


  - 如下情况下，结算后A,B玩家将分别获得8个筹码

    **Given：**
    
    | 进入摊牌阶段玩家（持有筹码，投注筹码） | 弃牌玩家（持有筹码，投注筹码） | 当局游戏筹码底池 |
    | -------------------------------------- | ------------------------------ | ---------------- |
    | A(100,4), B(100,4), C(100,4), D(100,4) |                                | 16               |
    
    **And**：A，B玩家的组合一样大
    **When：**进行筹码结算
    **Then：**
    
    | 玩家 | 持有筹码 |
    | ---- | -------- |
    | A    | 104      |
    | B    | 104      |
    | C    | 96       |
    | D    | 96       |

- 如有人ALL IN，则以ALL IN玩家参与的回合作为特殊结算点，依据结算点将奖池进行划分，并分别进行摊牌比较
  - 如下情况下，最终B玩家获得20筹码，A玩家获得9筹码

![all-in-example](/Users/fei/workspace/firenze-project/firenze-project-texas-holdem/README.assets/image-20210425092320352.png)

