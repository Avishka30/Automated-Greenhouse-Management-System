declare module 'eureka-js-client' {
  export class Eureka {
    constructor(config: Record<string, any>);
    start(callback?: (error?: Error) => void): void;
    stop(callback?: (error?: Error) => void): void;
  }
}

