import { AcGameObject } from './AcGameObject' // {} 解构赋值，如果导入文件是 export default 导出则不需要解构，因为全部都导出来了
import { Wall } from './Wall'

export class GameMap extends AcGameObject {
  constructor(ctx, parent) {
    super()

    this.ctx = ctx
    this.parent = parent
    this.L = 0 // 一个单位的长度

    this.rows = 13
    this.cols = 13

    this.innerWallsCount = 20
    this.walls = []
  }

  // 判断图的连通性(左下右上是否连通) --- Floyd Fill 算法
  checkConnectivity(g, sx, sy, tx, ty) {
    if (sx == tx && sy == ty) return true

    g[sx][sy] = true

    let dx = [-1, 0, 1, 0],
      dy = [0, 1, 0, -1]
    for (let i = 0; i < 4; i++) {
      let x = sx + dx[i]
      let y = sy + dy[i]

      if (!g[x][y] && this.checkConnectivity(g, x, y, tx, ty)) return true
    }

    return false
  }

  // 创建墙
  create_walls() {
    const g = []
    for (let r = 0; r < this.rows; r++) {
      g[r] = [] // 声明是二维数组
      for (let c = 0; c < this.cols; c++) {
        g[r][c] = false
      }
    }

    // 给四周加墙
    for (let r = 0; r < this.rows; r++) {
      g[r][0] = g[r][this.cols - 1] = true
    }

    for (let c = 0; c < this.cols; c++) {
      g[0][c] = g[this.rows - 1][c] = true
    }

    // 创建随机墙
    for (let i = 0; i < this.innerWallsCount / 2; i++) {
      for (let j = 0; j < 100; j++) {
        let r = parseInt(Math.random() * this.rows)
        let c = parseInt(Math.random() * this.cols)

        if (g[r][c] || g[c][r]) continue
        if ((r == this.rows - 2 && c == 1) || (r == 1 && c == this.cols - 2))
          continue

        g[r][c] = g[c][r] = true
        break
      }
    }

    const copy_g = JSON.parse(JSON.stringify(g))
    if (!this.checkConnectivity(copy_g, this.rows - 2, 1, 1, this.cols - 2))
      return false // 返回 false 不连通

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

  start() {
    for (let i = 0; i < 1000; i++) {
      if (this.create_walls()) break
    }
  }

  updateSize() {
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

  update() {
    // 特别注意：这俩方法顺序不能错
    // 隐式的在类成员方法里创建了函数 所以构造的时候就调用了这个方法
    this.updateSize() // 更新长度
    this.render() // 渲染
  }

  // 渲染函数
  render() {
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
