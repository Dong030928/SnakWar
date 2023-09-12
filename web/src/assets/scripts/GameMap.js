import { AcGameObject } from './AcGameObject' // {} 解构赋值，如果导入文件是 export default 导出则不需要解构，因为全部都导出来了
import { Snake } from './Snake'
import { Wall } from './Wall'

export class GameMap extends AcGameObject {
    constructor(ctx, parent, store) {
        super()

        this.ctx = ctx
        this.parent = parent
        this.store = store
        this.L = 0 // 一个单位的长度

        this.rows = 13
        this.cols = 14 // 列数增加一，保证了两条蛇不会走到同一个格子

        this.innerWallsCount = 20
        this.walls = []

        this.snakes = [
            new Snake({ id: 0, color: '#4876EC', r: this.rows - 2, c: 1 }, this),
            new Snake({ id: 1, color: '#F94848', r: 1, c: this.cols - 2 }, this),
        ]
    }

    // 创建墙
    create_walls () {
        const g = this.store.state.pk.game_map;

        // true 的地方加墙
        for (let r = 0; r < this.rows; r++) {
            for (let c = 0; c < this.cols; c++) {
                if (g[r][c] == true) {
                    // 如果当前为true 加墙
                    this.walls.push(new Wall(r, c, this))
                }
            }
        }

        return true // 返回 true 图连通
    }

    // 判断两条蛇有没有准备好进行下一步操作
    checkReady () {
        for (let snake of this.snakes) {
            if (snake.status !== 'idle') return false
            if (snake.direction === -1) return false
        }

        return true
    }

    // 两条蛇走下一步
    nextStep () {
        for (const snake of this.snakes) {
            snake.nextStep()
        }
    }

    // 键入操作
    addListeningEvents () {
        this.ctx.canvas.focus()

        this.ctx.canvas.addEventListener('keydown', (e) => {
            let d = -1;

            // 统一输入在后端处理(GameMap.js)，然后后端再发送给前端(PKIndexView.vue)
            if (e.key === 'w') d = 0;
            else if (e.key === 'd') d = 1;
            else if (e.key === 's') d = 2;
            else if (e.key === 'a') d = 3;

            if (d >= 0) {
                this.store.state.pk.socket.send(JSON.stringify({
                    event: "move",
                    direction: d
                }))
            }
        })
    }

    // 检查操作是否合法
    checkValid (nextCell) {
        // 下一步是否为墙
        for (const wall of this.walls) {
            if (nextCell.r === wall.r && nextCell.c === wall.c) {
                return false
            }
        }

        // 是否撞到自己
        for (const snake of this.snakes) {
            let k = snake.cells.length
            if (!snake.checkTailIncreasing()) {
                // 当蛇尾会前进的时候，蛇尾不要判断 k--
                k--
            }

            for (let i = 0; i < k; i++) {
                if (
                    // 如果下一个格子 与蛇身体任意一个部位重合 返回 false
                    snake.cells[i].r === nextCell.r &&
                    snake.cells[i].c === nextCell.c
                ) {
                    return false
                }
            }
        }

        return true
    }

    start () {
        this.create_walls()
        this.addListeningEvents()
    }

    updateSize () {
        // 加 parseInt 去除墙与墙之间的缝隙
        this.L = parseInt(
            Math.min(
                this.parent.clientWidth / this.cols,
                this.parent.clientHeight / this.rows
            )
        )

        this.ctx.canvas.width = this.L * this.cols
        this.ctx.canvas.height = this.L * this.rows
    }

    // 更新函数
    update () {
        // 特别注意：顺序不能错 渲染一定在最后
        // 隐式的在类成员方法里创建了函数 所以构造的时候就调用了这个方法
        this.updateSize() // 更新长度

        if (this.checkReady()) {
            this.nextStep()
        }

        this.render() // 渲染
    }

    // 渲染函数
    render () {
        const colorEven = '#AAD752',
            colorOdd = '#A2D048'

        for (let r = 0; r < this.rows; r++) {
            for (let c = 0; c < this.cols; c++) {
                if ((r + c) % 2 == 0) {
                    this.ctx.fillStyle = colorEven
                } else {
                    this.ctx.fillStyle = colorOdd
                }

                this.ctx.fillRect(c * this.L, r * this.L, this.L, this.L)
            }
        }
    }
}
