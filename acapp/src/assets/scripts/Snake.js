import { AcGameObject } from './AcGameObject'
import { Cells } from './Cells'

export class Snake extends AcGameObject {
    constructor(info, gamemap) {
        super()

        this.id = info.id
        this.color = info.color
        this.gamemap = gamemap

        this.cells = [new Cells(info.r, info.c)]
        this.nextCell = null // 下一步的目的地

        this.speed = 5
        this.step = 0 // 蛇的回合数

        this.direction = -1
        this.eyeDirection = 0
        if (this.id === 1) this.eyeDirection = 2
        this.status = 'idle' // 静止， move 移动 die 死亡

        this.dr = [-1, 0, 1, 0]
        this.dc = [0, 1, 0, -1]

        this.eye_dx = [
            [-1, 1],
            [1, 1],
            [1, -1],
            [-1, -1],
        ]

        this.eye_dy = [
            [-1, -1],
            [-1, 1],
            [1, 1],
            [1, -1],
        ]

        this.eps = 1e-2 // 误差
    }

    start () { }

    // 判断蛇在某回合是否变长
    checkTailIncreasing () {
        if (this.step < 10) return true
        if (this.step % 3 === 1) return true

        return false
    }

    setDirection (d) {
        this.direction = d
    }

    // 蛇的状态变为下一步
    nextStep () {
        const d = this.direction
        this.eyeDirection = d

        this.nextCell = new Cells(
            this.cells[0].r + this.dr[d],
            this.cells[0].c + this.dc[d]
        )

        this.direction = -1
        this.status = 'move'

        this.step++

        //  每个元素往后移一位
        const k = this.cells.length
        for (let i = k; i > 0; i--) {
            this.cells[i] = JSON.parse(JSON.stringify(this.cells[i - 1]))
        }

        // if (!this.gamemap.checkValid(this.nextCell)) {
        //   this.status = 'die'
        // }
    }

    updateMove () {
        const dx = this.nextCell.x - this.cells[0].x
        const dy = this.nextCell.y - this.cells[0].y
        const distance = Math.sqrt(dx * dx + dy * dy)

        // 注意 算的是每一帧的移动 因为一帧执行一次
        if (distance < this.eps) {
            this.status = 'idle'
            this.cells[0] = this.nextCell
            this.nextCell = null

            // 蛇如果不边长 砍掉蛇尾
            if (!this.checkTailIncreasing()) {
                this.cells.pop()
            }
        } else {
            // 每一秒的移动距离
            const moveDistance = (this.speed * this.timeDelta) / 1000

            // x方向的偏移量 = moveDistance * cos
            this.cells[0].x += (moveDistance * dx) / distance
            this.cells[0].y += (moveDistance * dy) / distance

            if (!this.checkTailIncreasing()) {
                const k = this.cells.length
                const tail = this.cells[k - 1],
                    tailTarget = this.cells[k - 2]
                const tail_dx = tailTarget.x - tail.x
                const tail_dy = tailTarget.y - tail.y
                tail.x += (moveDistance * tail_dx) / distance
                tail.y += (moveDistance * tail_dy) / distance
            }
        }
    }

    update () {
        if (this.status === 'move') {
            this.updateMove()
        }

        this.render()
    }

    render () {
        const L = this.gamemap.L
        const ctx = this.gamemap.ctx

        ctx.fillStyle = this.color

        if (this.status === 'die') {
            ctx.fillStyle = 'white'
        }

        for (let cell of this.cells) {
            // 画圆
            ctx.beginPath()
            ctx.arc(cell.x * L, cell.y * L, (L * 0.8) / 2, 0, Math.PI * 2)
            ctx.fill()
        }

        // 补全蛇的身体
        for (let i = 1; i < this.cells.length; i++) {
            let a = this.cells[i],
                b = this.cells[i - 1]

            if (Math.abs(a.x - b.x) < this.eps) {
                // 垂直方向
                ctx.fillRect(
                    (a.x - 0.5 + 0.1) * L,
                    Math.min(a.y, b.y) * L,
                    L * 0.8,
                    Math.abs(a.y - b.y) * L
                )
            } else {
                // 水平方向
                ctx.fillRect(
                    Math.min(a.x, b.x) * L,
                    (a.y - 0.5 + 0.1) * L,
                    Math.abs(a.x - b.x) * L,
                    L * 0.8
                )
            }
        }

        ctx.fillStyle = 'black'
        for (let i = 0; i < 2; i++) {
            const eye_x =
                (this.cells[0].x + this.eye_dx[this.eyeDirection][i] * 0.15) * L
            const eye_y =
                (this.cells[0].y + this.eye_dy[this.eyeDirection][i] * 0.15) * L

            ctx.beginPath()
            ctx.arc(eye_x, eye_y, L * 0.05, 0, Math.PI * 2)
            ctx.fill()
        }
    }
}
