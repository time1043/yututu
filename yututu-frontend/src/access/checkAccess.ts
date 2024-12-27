import ACCESS_ENUM from './accessEnum'

const checkAccess = (loginUser: any, needAccess = ACCESS_ENUM.NOT_LOGIN) => {
  // 当前用户权限 (若无loginUser则表示未登录)
  const loginUserAccess = loginUser?.userRole ?? ACCESS_ENUM.NOT_LOGIN
  // 当前功能不需要登陆 (放行)
  if (needAccess === ACCESS_ENUM.NOT_LOGIN) return true

  // 当前功能需要登陆 当前用户无登陆
  if (needAccess === ACCESS_ENUM.USER && loginUserAccess === ACCESS_ENUM.NOT_LOGIN) return false
  if (needAccess === ACCESS_ENUM.ADMIN && loginUserAccess !== ACCESS_ENUM.ADMIN) return false
  if (needAccess === ACCESS_ENUM.VIP && loginUserAccess !== ACCESS_ENUM.VIP) return false
  if (needAccess === ACCESS_ENUM.SVIP && loginUserAccess !== ACCESS_ENUM.SVIP) return false

  return true // 放行
}

export default checkAccess
