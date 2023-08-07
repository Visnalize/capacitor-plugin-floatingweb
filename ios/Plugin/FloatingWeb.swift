import Foundation

@objc public class FloatingWeb: NSObject {
    @objc public func echo(_ value: String) -> String {
        print(value)
        return value
    }
}
