export interface SakuraChatSystemSettings {
    mail: {
        smtp: {
            host: string,
            port: number,
            username: string,
            password: string,
            fromEmail: string
        }
    },
    userRegistration: {
        initialPoints: number
    }
}